package hr.tvz.groops.service.verification;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.event.notification.verification.*;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.model.PendingVerification;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.VerificationTypeEnum;
import hr.tvz.groops.repository.PendingVerificationRepository;
import hr.tvz.groops.service.security.AuthenticationService;
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
import java.util.Optional;

@Service
public class VerificationPublisherService {
    private static final Logger logger = LoggerFactory.getLogger(VerificationPublisherService.class);
    private final PendingVerificationRepository pendingVerificationRepository;
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public VerificationPublisherService(PendingVerificationRepository pendingVerificationRepository,
                                        AuthenticationService authenticationService,
                                        ApplicationEventPublisher applicationEventPublisher) {
        this.pendingVerificationRepository = pendingVerificationRepository;
        this.authenticationService = authenticationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyEmailCreate(@NotNull User user, @NotNull Instant now) {
        logger.debug("Generating pending verification for mail create and sending mail...");
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        VerificationTypeEnum verificationType = VerificationTypeEnum.MAIL_CREATE;
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(verificationType)
                .user(user)
                .createdBy(currentUser)
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.save(pendingVerification);
        sendEmailCreateVerificationEventAfterSuccessfulCommit(pendingVerification.getId(), pendingVerification.getUser().getId());
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyEmailChange(@NotNull User user, @NotNull Instant now) {
        logger.debug("Generating pending verification from mail change and sending mail...");
        VerificationTypeEnum verificationType = VerificationTypeEnum.MAIL_CHANGE;
        Optional<PendingVerification> existingPendingVerification = pendingVerificationRepository.findByUserAndVerificationType(user, verificationType);
        if (existingPendingVerification.isPresent()) {
            logger.debug("Deleting existing pending verification...");
            pendingVerificationRepository.delete(existingPendingVerification.get());
        }
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(verificationType)
                .user(user)
                .createdBy(currentUser)
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.saveAndFlush(pendingVerification);
        sendEmailChangeVerificationEventAfterSuccessfulCommit(pendingVerification.getId(), pendingVerification.getUser().getId());
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyPasswordForgot(@NotNull User user, @NotNull Instant now) {
        logger.debug("Generating pending verification from mail change and sending mail...");
        VerificationTypeEnum verificationType = VerificationTypeEnum.PASSWORD_FORGOT;
        Optional<PendingVerification> existingPendingVerification = pendingVerificationRepository.findByUserAndVerificationType(user, verificationType);
        if (existingPendingVerification.isPresent()) {
            logger.debug("Deleting existing pending verification...");
            pendingVerificationRepository.delete(existingPendingVerification.get());
        }
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(verificationType)
                .user(user)
                .createdBy(currentUser)
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.saveAndFlush(pendingVerification);
        sendPasswordForgotVerificationEventAfterSuccessfulCommit(pendingVerification.getId(), pendingVerification.getUser().getId());
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyPasswordChange(@NotNull User user, @NotNull Instant now) {
        logger.debug("Sending password change event...");
        PendingVerification pendingVerification = PendingVerification.builder()
                .verificationType(VerificationTypeEnum.PASSWORD_CHANGE)
                .user(user)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        pendingVerification = pendingVerificationRepository.saveAndFlush(pendingVerification);
        sendPasswordVerificationEventAfterSuccessfulCommit(pendingVerification.getId(), pendingVerification.getUser().getId());
    }

    @Transactional(timeout = TimeoutConstants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void publishVerificationEvent(PendingVerification pendingVerification) {
        VerificationEvent verificationEvent;
        switch (pendingVerification.getVerificationType()) {
            case MAIL_CHANGE:
                verificationEvent = new MailChangeVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId());
                break;
            case PASSWORD_CHANGE:
                verificationEvent = new PasswordChangeVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId());
                break;
            case MAIL_CREATE:
                verificationEvent = new MailCreateVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId());
                break;
            case PASSWORD_FORGOT:
                verificationEvent = new PasswordForgotVerificationEvent(this, pendingVerification.getId(), pendingVerification.getUser().getId());
                break;
            default:
                throw new InternalServerException("Unsupported verification type");
        }
        sendEmailForVerificationEventAfterSuccessfulCommit(verificationEvent);
    }

    private void sendEmailForVerificationEventAfterSuccessfulCommit(VerificationEvent event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(event);
            }
        });
    }

    private void sendEmailCreateVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId, @NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailCreateVerificationEvent(this, pendingVerificationId, userId));
            }
        });
    }

    private void sendEmailChangeVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId, @NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailChangeVerificationEvent(this, pendingVerificationId, userId));
            }
        });
    }

    private void sendPasswordVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId, @NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new PasswordChangeVerificationEvent(this, pendingVerificationId, userId));
            }
        });
    }

    private void sendPasswordForgotVerificationEventAfterSuccessfulCommit(@NotNull Long pendingVerificationId, @NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new PasswordForgotVerificationEvent(this, pendingVerificationId, userId));
            }
        });
    }

}
