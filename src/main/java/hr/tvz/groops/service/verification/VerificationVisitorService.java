package hr.tvz.groops.service.verification;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.criteria.Searchable;
import hr.tvz.groops.event.mail.MailEvent;
import hr.tvz.groops.event.notification.verification.MailChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.MailVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordForgotVerificationEvent;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.MailMessage;
import hr.tvz.groops.model.User;
import hr.tvz.groops.repository.UserRepository;
import hr.tvz.groops.security.constants.RoleConstants;
import hr.tvz.groops.service.security.AuthenticationService;
import hr.tvz.groops.service.url.URLService;
import hr.tvz.groops.service.mail.MailCreatorService;
import hr.tvz.groops.service.template.TemplateService;
import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final JWTService passwordForgotJWTService;
    private final MailCreatorService mailCreatorService;
    private final AuthenticationService authenticationService;
    private final TemplateService thymeleafTemplateService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final String VERIFICATION_TEMPLATE;
    private final URLService urlService;
    private final UserRepository userRepository;

    @Autowired
    public VerificationVisitorService(JWTService mailChangeJWTService,
                                      JWTService mailCreateJWTService,
                                      JWTService passwordChangeJWTService,
                                      JWTService passwordForgotJWTService,
                                      MailCreatorService mailCreatorService,
                                      AuthenticationService authenticationService,
                                      TemplateService thymeleafTemplateService,
                                      ApplicationEventPublisher applicationEventPublisher,
                                      @Value("${groops.thymeleaf.verification.template.html}") String VERIFICATION_TEMPLATE,
                                      URLService urlService,
                                      UserRepository userRepository) {
        this.mailChangeJWTService = mailChangeJWTService;
        this.mailCreateJWTService = mailCreateJWTService;
        this.passwordChangeJWTService = passwordChangeJWTService;
        this.passwordForgotJWTService = passwordForgotJWTService;
        this.mailCreatorService = mailCreatorService;
        this.authenticationService = authenticationService;
        this.thymeleafTemplateService = thymeleafTemplateService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.VERIFICATION_TEMPLATE = VERIFICATION_TEMPLATE;
        this.urlService = urlService;
        this.userRepository = userRepository;
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailCreateVerification(MailVerificationEvent mailVerificationEvent) {
        User recipient = findUserEntityById(mailVerificationEvent.getUserId(), userRepository);
        String b64Token = mailCreateJWTService.generateTokenBase64(recipient.getId(), recipient.getUsername(), RoleConstants.ROLE_MAIL_CREATE);
        String parameter = mailCreateJWTService.getParameterName();
        if (parameter == null) {
            throw new InternalServerException("Parameter must not be null");
        }
        String baseURL = urlService.getBackendBaseURL() + "templates/mail/create";
        finalizeMailCreation(baseURL, "Groops - mail verification", "mail", parameter, b64Token, recipient);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitMailChangeVerification(MailChangeVerificationEvent mailChangeVerificationEvent) {
        User recipient = findUserEntityById(mailChangeVerificationEvent.getUserId(), userRepository);
        String b64Token = mailChangeJWTService.generateTokenBase64(recipient.getId(), recipient.getUsername(), RoleConstants.ROLE_MAIL_CHANGE);
        String parameter = mailChangeJWTService.getParameterName();
        if (parameter == null) {
            throw new InternalServerException("Parameter must not be null");
        }
        String baseURL = urlService.getBackendBaseURL() + "templates/mail/change";
        finalizeMailCreation(baseURL, "Groops - mail change verification", "mail change", parameter, b64Token, recipient);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitPasswordForgotVerification(PasswordForgotVerificationEvent passwordForgotVerificationEvent) {
        User recipient = findUserEntityById(passwordForgotVerificationEvent.getUserId(), userRepository);
        String b64Token = passwordForgotJWTService.generateTokenBase64(recipient.getId(), recipient.getUsername(), RoleConstants.ROLE_PASSWORD_FORGOT);
        String parameter = passwordForgotJWTService.getParameterName();
        if (parameter == null) {
            throw new InternalServerException("Parameter must not be null");
        }
        String baseURL = urlService.getFrontendBaseURL() + "password-forgot-change";
        finalizeMailCreation(baseURL, "Groops - password forgot verification", "password forgot", parameter, b64Token, recipient);
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void visitPasswordVerification(PasswordChangeVerificationEvent passwordChangeVerificationEvent) {
        User recipient = findUserEntityById(passwordChangeVerificationEvent.getUserId(), userRepository);
        String b64Token = passwordChangeJWTService.generateTokenBase64(recipient.getId(), recipient.getUsername(), RoleConstants.ROLE_PASSWORD_CHANGE);
        String parameter = passwordChangeJWTService.getParameterName();
        if (parameter == null) {
            throw new InternalServerException("Parameter must not be null");
        }
        String baseURL = urlService.getBackendBaseURL() + "templates/password/change";
        finalizeMailCreation(baseURL, "Groops - password verification", "password change", parameter, b64Token, recipient);
    }

    private void finalizeMailCreation(String baseURL, String subject, String verificationPredicate, String parameter, String b64Token, User recipient) {
        String templateHtml = thymeleafTemplateService.generateVerificationTemplateHtml(this.VERIFICATION_TEMPLATE, verificationPredicate, baseURL, parameter, b64Token);
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        User sender = findUserEntityByUsername(currentUser, userRepository);
        MailMessage mailMessage = mailCreatorService.createAndPublishMails(sender, subject, templateHtml, templateHtml, currentUser, now(), recipient);
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
