package hr.tvz.groops.service.mail;

import hr.tvz.groops.constants.TimeoutConstants;
import hr.tvz.groops.model.Mail;
import hr.tvz.groops.model.MailExceptionLog;
import hr.tvz.groops.model.MailMessage;
import hr.tvz.groops.model.User;
import hr.tvz.groops.model.enums.MailStatusEnum;
import hr.tvz.groops.repository.MailExceptionLogRepository;
import hr.tvz.groops.repository.MailMessageRepository;
import hr.tvz.groops.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static hr.tvz.groops.util.TimeUtils.now;

@Service
public class MailCreatorService {

    private final MailRepository mailRepository;
    private final MailExceptionLogRepository mailExceptionLogRepository;
    private final MailMessageRepository mailMessageRepository;
    private final Long expiresDays;

    @Autowired
    public MailCreatorService(MailRepository mailRepository,
                              MailExceptionLogRepository mailExceptionLogRepository,
                              MailMessageRepository mailMessageRepository,
                              @Value("${groops.mail.expires.days}") Long expiresDays) {
        this.mailRepository = mailRepository;
        this.mailExceptionLogRepository = mailExceptionLogRepository;
        this.mailMessageRepository = mailMessageRepository;
        this.expiresDays = expiresDays;
    }

    @Transactional(timeout = TimeoutConstants.DEFAULT_TIMEOUT, propagation = Propagation.MANDATORY)
    public MailMessage createAndPublishMails(User sender, String subject, String htmlMessage, String txtMessage, String createdBy, Instant createdTs, User... recipients) {
        MailMessage mailMessage = MailMessage.builder()
                .subject(subject)
                .htmlMessage(htmlMessage)
                .txtMessage(txtMessage)
                .createdBy(createdBy)
                .createdTs(createdTs)
                .build();

        mailMessage = mailMessageRepository.save(mailMessage);

        List<Mail> mails = new ArrayList<>();

        for (User recipient : recipients) {
            Mail mail = Mail.builder()
                    .recipient(recipient)
                    .sender(sender)
                    .mailMessage(mailMessage)
                    .mailStatus(MailStatusEnum.PENDING)
                    .expires(createdTs.plus(expiresDays, ChronoUnit.DAYS))
                    .createdBy(createdBy)
                    .createdTs(createdTs)
                    .build();

            mails.add(mail);
        }

        mailRepository.saveAllAndFlush(mails);
        return mailMessage;
    }


    public List<Tuple> findIdSenderIdRecipientIdMailMessageIdByMailStatus(MailStatusEnum mailStatus) {
        return mailRepository.findIdSenderIdRecipientIdMailMessageIdByMailStatus(mailStatus);
    }

    @Transactional(timeout = TimeoutConstants.SHORT_TIMEOUT, propagation = Propagation.MANDATORY)
    public void createMailExceptionForMail(Mail mail, String message, String stackTrace, String createdBy, Instant createdTs) {
        MailExceptionLog mailExceptionLog = MailExceptionLog.builder()
                .mail(mail)
                .message(message)
                .stackTrace(stackTrace)
                .createdBy(createdBy)
                .createdTs(createdTs)
                .build();

        mailExceptionLogRepository.save(mailExceptionLog);
    }

    @Transactional(timeout = TimeoutConstants.MEDIUM_TIMEOUT)
    public void deleteAllExpired() {
        mailRepository.deleteAllExpired(now());
    }

    @Transactional(timeout = TimeoutConstants.MEDIUM_TIMEOUT)
    public void deleteAllUnlinkedMailMessages() {
        mailMessageRepository.deleteAllUnlinked();
    }
}
