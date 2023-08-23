package hr.tvz.groops.security.filter;

import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.service.AuthenticationService;
import hr.tvz.groops.service.token.JWTService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Component
public class AppJWTVerifier extends JWTVerifier {
    private static final Logger logger = LoggerFactory.getLogger(AppJWTVerifier.class);
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public AppJWTVerifier(AuthenticationService authenticationService, JWTService appJWTService, UserRepository userRepository) {
        super(appJWTService);
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    protected void handleTokenRequest(HttpServletRequest request, String requestToken) {
        super.handleTokenRequest(request, requestToken);
        Long currentUserId = authenticationService.getCurrentLoggedInUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new AccessDeniedException("Unauthorized"));
        if (!user.getVerified()) {
            logger.debug("Unverified user with id: {}", user.getId());
            throw new AccessDeniedException("Unauthorized");
        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        return authenticationService.andMatchesAny(request);
    }
}
