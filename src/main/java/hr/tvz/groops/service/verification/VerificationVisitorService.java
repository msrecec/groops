package hr.tvz.groops.service.verification;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.event.notification.verification.MailChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.MailVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordChangeVerificationEvent;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VerificationVisitorService implements Searchable {
    private final JWTService mailChangeJWTService;
    private final JWTService mailCreateJWTService;
    private final JWTService passwordChangeJWTService;
    private final UserRepository userRepository;

    @Autowired
    public VerificationVisitorService(JWTService mailChangeJWTService,
                                      JWTService mailCreateJWTService,
                                      JWTService passwordChangeJWTService,
                                      UserRepository userRepository) {
        this.mailChangeJWTService = mailChangeJWTService;
        this.mailCreateJWTService = mailCreateJWTService;
        this.passwordChangeJWTService = passwordChangeJWTService;
        this.userRepository = userRepository;
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailCreateVerification(MailVerificationEvent mailVerificationEvent) {
        User user = findUserEntityById(mailVerificationEvent.getUserId(), userRepository);
        mailCreateJWTService.generateToken(user.getUsername(), user.getEmail(), List.of(RoleConstants.ROLE_MAIL_CREATE));
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailChangeVerification(MailChangeVerificationEvent mailChangeVerificationEvent) {
        User user = findUserEntityById(mailChangeVerificationEvent.getUserId(), userRepository);
        mailChangeJWTService.generateToken(user.getUsername(), user.getEmail(), List.of(RoleConstants.ROLE_MAIL_CREATE));
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitPasswordVerification(PasswordChangeVerificationEvent passwordChangeVerificationEvent) {
        User user = findUserEntityById(passwordChangeVerificationEvent.getUserId(), userRepository);
        passwordChangeJWTService.generateToken(user.getUsername(), user.getEmail(), List.of(RoleConstants.ROLE_MAIL_CREATE));
    }
}
