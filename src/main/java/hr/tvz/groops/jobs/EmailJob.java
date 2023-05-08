package hr.tvz.groops.jobs;

import hr.tvz.groops.event.mail.MailEvent;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.jobs.annotation.JobSecurityContext;
import hr.tvz.groops.model.enums.MailStatusEnum;
import hr.tvz.groops.service.mail.MailCreatorService;
import hr.tvz.groops.service.mail.MailJobService;
import hr.tvz.groops.service.logging.MDCService;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.persistence.Tuple;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@DisallowConcurrentExecution
@ConditionalOnProperty(prefix = "groops.mail", name = "scheduler", havingValue = "true")
public class EmailJob extends BaseJob implements ApplicationListener<MailEvent>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    public static final String EMAIL_SCHEDULER_JOB_IDENTITY = "emailJob";
    public static final String EMAIL_SCHEDULER_JOB_DESCRIPTION = "Email job";
    private final BlockingQueue<MailEvent> mailQueue = new LinkedBlockingQueue<>();
    private final MailJobService emailJobService;
    private final MailCreatorService mailCreatorService;
    private final MDCService mdcService;

    @Autowired
    public EmailJob(MailJobService emailJobService, MailCreatorService mailCreatorService, MDCService mdcService) {
        this.emailJobService = emailJobService;
        this.mailCreatorService = mailCreatorService;
        this.mdcService = mdcService;
    }

    @Override
    public void onApplicationEvent(@NotNull MailEvent mailEvent) {
        mailQueue.add(mailEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            mdcService.setMDCUsername(EMAIL_SCHEDULER_JOB_IDENTITY);
            logger.debug("Filling pending mails...");

            List<Tuple> pendingMails = mailCreatorService.findIdSenderIdRecipientIdMailMessageIdByMailStatus(MailStatusEnum.PENDING);
            for (Tuple pendingMailTuple : pendingMails) {
                mailQueue.add(createFromTuple(pendingMailTuple));
            }

            logger.debug(String.format("Filled %d pending mails", pendingMails.size()));

        } finally {
            mdcService.clearMDCUsername();
        }
    }

    private MailEvent createFromTuple(Tuple tuple) {
        return new MailEvent(this, (Long) tuple.get(0), (Long) tuple.get(1), (Long) tuple.get(2));
    }

    @Override
    @JobSecurityContext
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = getJobName(context);
        try {
            mdcService.setMDCUsername(jobName);
            logger.debug("Entering email scheduler loop...");
            for (; ; ) {
                try {
                    MailEvent mailEvent = mailQueue.take();
                    logger.debug("Sending a mail message...");
                    emailJobService.sendMailFromEvent(mailEvent);

                } catch (InterruptedException interruptedException) {
                    logger.error(ExceptionEnum.INTERRUPTED_EXCEPTION.getFullMessage(), interruptedException);
                    logger.debug("Exiting email scheduler loop due to interrupted exception...");
                    break;

                } catch (Exception exception) {
                    logger.error(ExceptionEnum.EXCEPTION.getFullMessage(), exception);
                    logger.debug("Exiting email scheduler loop due to exception...");
                    break;

                }
            }
        } finally {
            mdcService.clearMDCUsername();
            logger.debug("Exited email scheduler loop");
        }
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
