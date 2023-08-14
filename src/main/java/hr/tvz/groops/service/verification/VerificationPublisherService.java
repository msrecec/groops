package hr.tvz.groops.service.verification;

import hr.tvz.groops.event.notification.verification.MailChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.MailCreateVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordChangeVerificationEvent;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.EmailVerificationCode;
import hr.tvz.groops.model.PendingVerification;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.constants.Constants;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import hr.tvz.groops.repository.EmailVerificationCodeRepository;
import hr.tvz.groops.repository.PendingVerificationRepository;
import hr.tvz.groops.service.AuthenticationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class VerificationPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(VerificationPublisherService.class);
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PendingVerificationRepository pendingVerificationRepository;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public VerificationPublisherService(EmailVerificationCodeRepository emailVerificationCodeRepository, PendingVerificationRepository pendingVerificationRepository, AuthenticationService authenticationService, ApplicationEventPublisher applicationEventPublisher) {
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.pendingVerificationRepository = pendingVerificationRepository;
        this.authenticationService = authenticationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(timeout = Constants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyEmail(@NotNull User user, @NotNull Instant now, VerificationTypeEnum verificationType) {
        logger.debug("Generating verification code and sending mail...");
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        String code = RandomStringUtils.randomAlphabetic(10);
        EmailVerificationCode emailVerificationCode = EmailVerificationCode.builder()
                .code(code)
                .createdBy(currentUser)
                .createdTs(now)
                .build();
        emailVerificationCodeRepository.save(emailVerificationCode);
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(verificationType)
                .user(user)
                .createdBy(currentUser)
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.saveAndFlush(pendingVerification);
        sendEmailVerificationEventAfterSuccessfulCommit(pendingVerification.getId(), pendingVerification.getVerificationType());
    }

    @Transactional(timeout = Constants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyPasswordChange(@NotNull User user, @NotNull Instant now) {
        logger.debug("Sending password change event...");
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(VerificationTypeEnum.PASSWORD_CHANGE)
                .user(user)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.saveAndFlush(pendingVerification);
        sendPasswordVerificationEventAfterSuccessfulCommit(pendingVerification.getId());
    }

    private void sendEmailVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId, @NotNull VerificationTypeEnum verificationTypeEnum) {
        switch (verificationTypeEnum) {
            case MAIL:
                sendEmailCreateVerificationEventAfterSuccessfulCommit(pendingVerificationId);
                break;
            case MAIL_CHANGE:
                sendEmailChangeVerificationEventAfterSuccessfulCommit(pendingVerificationId);
                break;
            default:
                throw new InternalServerException("Non supported verification type", new Throwable());
        }
    }

    private void sendEmailCreateVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailCreateVerificationEvent(this, pendingVerificationId));
            }
        });
    }

    private void sendEmailChangeVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailChangeVerificationEvent(this, pendingVerificationId));
            }
        });
    }

    private void sendPasswordVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new PasswordChangeVerificationEvent(this, pendingVerificationId));
            }
        });
    }

}
