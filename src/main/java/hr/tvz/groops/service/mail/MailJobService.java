package hr.tvz.groops.service.mail;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.event.mail.MailEvent;
import hr.tvz.groops.model.Mail;
import hr.tvz.groops.model.enums.MailStatusEnum;
import hr.tvz.groops.repository.MailRepository;
import hr.tvz.groops.service.security.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static hr.tvz.groops.exception.ExceptionEnum.EMAIL_EXCEPTION;
import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class MailJobService {

    private static final Logger logger = LoggerFactory.getLogger(MailJobService.class);

    private final MailRepository mailRepository;
    private final MailClientService mailClientService;
    private final MailCreatorService mailCreatorService;

    private final AuthenticationService authenticationService;

    @Autowired
    public MailJobService(MailRepository mailRepository,
                          MailClientService mailClientService,
                          MailCreatorService mailCreatorService,
                          AuthenticationService authenticationService) {
        this.mailRepository = mailRepository;
        this.mailClientService = mailClientService;
        this.mailCreatorService = mailCreatorService;
        this.authenticationService = authenticationService;
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT)
    public void sendMailFromEvent(MailEvent emailEvent) {
        Instant now = now();
        String currentUser = authenticationService.getCurrentLoggedInUserUsername();
        Optional<Mail> mailOptional = mailRepository.findMailByMailMessageIdAndRecipientIdAndSenderIdAndMailStatus(
                emailEvent.getMailMessageId(),
                emailEvent.getRecipientId(),
                emailEvent.getSenderId(),
                MailStatusEnum.PENDING
        );

        if (mailOptional.isEmpty()) {
            logger.warn(String.format("Mail not found, can't send mail for recipientId %d, messageId %d, senderId %d and status %s",
                    emailEvent.getRecipientId(), emailEvent.getMailMessageId(), emailEvent.getSenderId(), MailStatusEnum.PENDING.name()
            ));
            return;
        }

        Mail mail = mailOptional.get();

        if (mail.getSender().getEmail() == null || mail.getRecipient().getEmail() == null) {
            boolean first = false;
            boolean multiple = false;
            if (mail.getSender().getEmail() == null) {
                logger.warn(String.format("Sender %s has no email", mail.getSender().getUsername()));
                first = true;
            }
            if (mail.getRecipient().getEmail() == null) {
                logger.warn(String.format("Recipient %s has no email", mail.getRecipient().getUsername()));
                multiple = first;
            }
            logger.warn(String.format("Email %s not present, skipping email sending...", multiple ? "addresses" : "address"));
            return;
        }

        final int maxFailedAttempts = 3;
        int failedAttempt = 0;

        for (; ; ) {
            try {
                mailClientService.sendTo(
                        mail.getSender().getEmail(),
                        mail.getRecipient().getEmail(),
                        mail.getMailMessage().getSubject(),
                        mail.getMailMessage().getHtmlMessage(),
                        mail.getMailMessage().getTxtMessage()
                );
            } catch (Exception exception) {
                if (++failedAttempt < maxFailedAttempts) {
                    logger.info(String.format("Retry number %d of the mail send attempt...", failedAttempt));
                    continue;
                }
                logger.error(EMAIL_EXCEPTION.getFullMessage(), exception);

                String message = exception.getMessage();
                String stackTrace = Arrays.toString(exception.getStackTrace());
                mailCreatorService.createMailExceptionForMail(mail, message, stackTrace, currentUser, now);

                mail.setMailStatus(MailStatusEnum.FAILED);
                mail.setModifiedBy(currentUser);
                mail.setModifiedTs(now);
                mailRepository.saveAndFlush(mail);

                logger.warn(String.format("Unable to send email to user %s for message with id %d", mail.getRecipient().getEmail(), mail.getMailMessage().getId()));
                break;
            }

            mailRepository.delete(mail);
            logger.debug("Successfully sent mail");
            break;
        }
    }
}
