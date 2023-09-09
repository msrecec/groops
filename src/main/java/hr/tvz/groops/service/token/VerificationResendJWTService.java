package hr.tvz.groops.service.token;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.dto.response.JWTDto;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.security.token.JwtConfig;
import hr.tvz.groops.service.security.AuthenticationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class VerificationResendJWTService extends VerificationJWTService implements VerificationResendService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public VerificationResendJWTService(JwtConfig verificationResendJwtConfig,
                                        UserRepository userRepository,
                                        AuthenticationService authenticationService) {
        super(verificationResendJwtConfig);
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public void setResendTokenForUser(String username, HttpServletResponse response) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return;
        }

        JWTDto token = getToken(user.get().getId(), user.get().getUsername(), RoleConstants.ROLE_VERIFICATION_RESEND);
        String header = getResponseHeader();
        response.setHeader(header, token.getToken());
    }

    @Override
    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT)
    public void setResendTokenForUser(HttpServletResponse response) {
        setResendTokenForUser(authenticationService.getCurrentLoggedInUserUsername(), response);
    }
}
