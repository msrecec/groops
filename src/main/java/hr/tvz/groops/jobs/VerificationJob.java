package hr.tvz.groops.jobs;

import hr.tvz.groops.event.notification.verification.VerificationEvent;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.jobs.annotation.JobSecurityContext;
import hr.tvz.groops.service.logging.MDCService;
import hr.tvz.groops.service.UserService;
import hr.tvz.groops.service.verification.VerificationVisitorService;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@DisallowConcurrentExecution
public class VerificationJob extends BaseJob implements ApplicationListener<VerificationEvent>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(VerificationJob.class);
    public static final String VERIFICATION_SCHEDULER_JOB_IDENTITY = "verificationJob";
    public static final String VERIFICATION_SCHEDULER_JOB_DESCRIPTION = "Verification job";
    private final BlockingQueue<VerificationEvent> verificationEventQueue = new LinkedBlockingQueue<>();
    private final VerificationVisitorService verificationVisitorService;
    private final UserService userService;
    private final MDCService mdcService;

    @Autowired
    public VerificationJob(VerificationVisitorService verificationVisitorService, UserService userService, MDCService mdcService) {
        this.verificationVisitorService = verificationVisitorService;
        this.userService = userService;
        this.mdcService = mdcService;
    }

    @Override
    public void onApplicationEvent(@NotNull VerificationEvent verificationEvent) {
        verificationEventQueue.add(verificationEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            mdcService.setMDCUsername(VERIFICATION_SCHEDULER_JOB_IDENTITY);
            logger.debug("Filling pending verifications...");

            List<VerificationEvent> verificationEvents = userService.findNonVerifiedEmailUserEvents();

            logger.debug(String.format("Filled %d pending verifications", verificationEvents.size()));

        } finally {
            mdcService.clearMDCUsername();
        }
    }

    @Override
    @JobSecurityContext
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = getJobName(context);
        try {
            mdcService.setMDCUsername(jobName);
            logger.debug("Entering verification scheduler loop...");
            for (; ; ) {
                try {
                    VerificationEvent verificationEvent = verificationEventQueue.take();
                    logger.debug("Handling verification event for user with id {}...", verificationEvent.getUserId());
                    verificationEvent.accept(verificationVisitorService);
                    logger.debug("Finished handling verification event for user with id {}...", verificationEvent.getUserId());

                } catch (InterruptedException interruptedException) {
                    logger.error(ExceptionEnum.INTERRUPTED_EXCEPTION.getFullMessage(), interruptedException);
                    logger.debug("Exiting verification scheduler loop due to interrupted exception...");
                    break;

                } catch (Exception exception) {
                    logger.error(ExceptionEnum.EXCEPTION.getFullMessage(), exception);
                    logger.debug("Exiting verification scheduler loop due to exception...");
                    break;

                }
            }
        } finally {
            mdcService.clearMDCUsername();
            logger.debug("Exited verification scheduler loop");
        }
    }

    @Override
    public boolean isRunning() {
        return true;
    }

}
