package hr.tvz.groops.service.verification;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.event.mail.MailEvent;
import hr.tvz.groops.event.notification.verification.MailChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.MailVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordChangeVerificationEvent;
import hr.tvz.groops.model.MailMessage;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.service.AuthenticationService;
import hr.tvz.groops.service.URLService;
import hr.tvz.groops.service.mail.MailCreatorService;
import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class VerificationVisitorService implements Searchable {
    private final JWTService mailChangeJWTService;
    private final JWTService mailCreateJWTService;
    private final JWTService passwordChangeJWTService;
    private final MailCreatorService mailCreatorService;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final URLService urlService;
    private final UserRepository userRepository;

    @Autowired
    public VerificationVisitorService(JWTService mailChangeJWTService,
                                      JWTService mailCreateJWTService,
                                      JWTService passwordChangeJWTService,
                                      MailCreatorService mailCreatorService,
                                      AuthenticationService authenticationService,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      URLService urlService,
                                      UserRepository userRepository) {
        this.mailChangeJWTService = mailChangeJWTService;
        this.mailCreateJWTService = mailCreateJWTService;
        this.passwordChangeJWTService = passwordChangeJWTService;
        this.mailCreatorService = mailCreatorService;
        this.authenticationService = authenticationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.urlService = urlService;
        this.userRepository = userRepository;
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailCreateVerification(MailVerificationEvent mailVerificationEvent) {
        User recipient = findUserEntityById(mailVerificationEvent.getUserId(), userRepository);
        String tokenB64 = mailCreateJWTService.generateTokenBase64(recipient.getUsername(), recipient.getEmail(), RoleConstants.ROLE_MAIL_CREATE);
        String parameter = mailCreateJWTService.getParameterName();
        String baseURL = urlService.getApplicationBaseURL() + "templates/mail/create" + "?" + parameter + "=" + tokenB64;
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        User sender = findUserEntityByUsername(currentUser, userRepository);
        MailMessage mailMessage = mailCreatorService.createAndPublishMails(sender, "confirm email", baseURL, baseURL, currentUser, now(), recipient);
        sendEmailEventAfterSuccessfulCommit(recipient, sender, mailMessage);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailChangeVerification(MailChangeVerificationEvent mailChangeVerificationEvent) {
        User recipient = findUserEntityById(mailChangeVerificationEvent.getUserId(), userRepository);
        String tokenB64 = mailChangeJWTService.generateTokenBase64(recipient.getUsername(), recipient.getEmail(), RoleConstants.ROLE_MAIL_CHANGE);
        String parameter = mailChangeJWTService.getParameterName();
        String baseURL = urlService.getApplicationBaseURL() + "templates/mail/change" + "?" + parameter + "=" + tokenB64;
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        User sender = findUserEntityByUsername(currentUser, userRepository);
        MailMessage mailMessage = mailCreatorService.createAndPublishMails(sender, "confirm email", baseURL, baseURL, currentUser, now(), recipient);
        sendEmailEventAfterSuccessfulCommit(recipient, sender, mailMessage);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitPasswordVerification(PasswordChangeVerificationEvent passwordChangeVerificationEvent) {
        User recipient = findUserEntityById(passwordChangeVerificationEvent.getUserId(), userRepository);
        String tokenB64 = passwordChangeJWTService.generateTokenBase64(recipient.getUsername(), recipient.getEmail(), RoleConstants.ROLE_PASSWORD_CHANGE);
        String parameter = passwordChangeJWTService.getParameterName();
        String baseURL = urlService.getApplicationBaseURL() + "templates/password/change" + "?" + parameter + "=" + tokenB64;
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        User sender = findUserEntityByUsername(currentUser, userRepository);
        MailMessage mailMessage = mailCreatorService.createAndPublishMails(sender, "confirm email", baseURL, baseURL, currentUser, now(), recipient);
        sendEmailEventAfterSuccessfulCommit(recipient, sender, mailMessage);
    }

    private void sendEmailEventAfterSuccessfulCommit(User recipient, User sender, MailMessage mailMessage) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailEvent(this,
                        sender.getId(),
                        recipient.getId(),
                        mailMessage.getId()
                ));
            }
        });
    }
}
