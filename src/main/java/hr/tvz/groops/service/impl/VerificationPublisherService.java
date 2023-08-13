package hr.tvz.groops.service.impl;

import hr.tvz.groops.event.notification.verification.MailVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordVerificationEvent;
import hr.tvz.groops.model.EmailVerificationCode;
import hr.tvz.groops.model.constants.Constants;
import hr.tvz.groops.repository.EmailVerificationCodeRepository;
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
    private final AuthenticationService authenticationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public VerificationPublisherService(EmailVerificationCodeRepository emailVerificationCodeRepository, AuthenticationService authenticationService, ApplicationEventPublisher applicationEventPublisher) {
        this.emailVerificationCodeRepository = emailVerificationCodeRepository;
        this.authenticationService = authenticationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(timeout = Constants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyEmail(@NotNull Long userId, @NotNull Instant now) {
        logger.debug("Generating verification code and sending mail...");
        String code = RandomStringUtils.randomAlphabetic(10);
        EmailVerificationCode emailVerificationCode = EmailVerificationCode.builder()
                .code(code)
                .createdBy(authenticationService.getCurrentLoggedInUserUsername())
                .createdTs(now)
                .build();
        emailVerificationCodeRepository.saveAndFlush(emailVerificationCode);
        sendEmailVerificationEventAfterSuccessfulCommit(userId);
    }

    @Transactional(timeout = Constants.TINY_TIMEOUT, propagation = Propagation.MANDATORY)
    public void verifyPasswordChange(@NotNull Long userId) {
        logger.debug("Sending password change event...");
        sendPasswordVerificationEventAfterSuccessfulCommit(userId);
    }

    private void sendEmailVerificationEventAfterSuccessfulCommit(@NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new MailVerificationEvent(this, userId));
            }
        });
    }
    private void sendPasswordVerificationEventAfterSuccessfulCommit(@NotNull Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new PasswordVerificationEvent(this, userId));
            }
        });
    }

}
