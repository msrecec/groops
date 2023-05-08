package hr.tvz.groops.jobs.config;

import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.jobs.EmailJob;
import hr.tvz.groops.jobs.VerificationJob;
import hr.tvz.groops.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.TimeZone;

import static hr.tvz.groops.jobs.EmailJob.EMAIL_SCHEDULER_JOB_DESCRIPTION;
import static hr.tvz.groops.jobs.EmailJob.EMAIL_SCHEDULER_JOB_IDENTITY;
import static hr.tvz.groops.jobs.VerificationJob.VERIFICATION_SCHEDULER_JOB_DESCRIPTION;
import static hr.tvz.groops.jobs.VerificationJob.VERIFICATION_SCHEDULER_JOB_IDENTITY;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    public static final String JOB_ID = "id";
    private final ApplicationContext applicationContext;
    private final UserService userService;
    private final String MAIL;
    private final String CRON_PATTERN_VERIFIER;
    private final String CRON_PATTERN_MAIL;
    private final String TIME_ZONE_ID;

    @Autowired
    public SchedulerConfig(ApplicationContext applicationContext,
                           UserService userService,
                           @Value("${spring.mail.username}") String MAIL,
                           @Value("${groops.cron.pattern.notification.verifier}") String CRON_PATTERN_VERIFIER,
                           @Value("${groops.cron.pattern.mail}") String CRON_PATTERN_MAIL,
                           @Value("${time.zone}") String TIME_ZONE_ID) {
        this.applicationContext = applicationContext;
        this.userService = userService;
        this.MAIL = MAIL;
        this.CRON_PATTERN_VERIFIER = CRON_PATTERN_VERIFIER;
        this.CRON_PATTERN_MAIL = CRON_PATTERN_MAIL;
        this.TIME_ZONE_ID = TIME_ZONE_ID;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        logger.trace("Initializing scheduler bean with singleton job factory...");
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.setJobFactory(new SingletonJobFactory(applicationContext));
        return scheduler;
    }


    @Bean("verificationJobDetail")
    public JobDetail verificationJobDetail() {
        String jobEmail = this.MAIL;
        validateJobEmailAddress(jobEmail);
        Long jobId = userService.createJobUserByNameLockByPessimisticWriteIfNotExistsAndGetId(VERIFICATION_SCHEDULER_JOB_IDENTITY, jobEmail, VERIFICATION_SCHEDULER_JOB_DESCRIPTION);
        return JobBuilder
                .newJob(VerificationJob.class)
                .withIdentity(initializeAndLogBeanName(VERIFICATION_SCHEDULER_JOB_IDENTITY))
                .withDescription(VERIFICATION_SCHEDULER_JOB_DESCRIPTION)
                .usingJobData(JOB_ID, jobId)
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    public Trigger verificationTrigger(JobDetail verificationJobDetail) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity(initializeAndLogBeanName("verificationJobTrigger"))
                .withDescription(VERIFICATION_SCHEDULER_JOB_DESCRIPTION)
                .forJob(verificationJobDetail)
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(CRON_PATTERN_VERIFIER)
                        .inTimeZone(TimeZone.getTimeZone(TIME_ZONE_ID))
                ).build();
    }

    @Bean("mailJobDetail")
    public JobDetail mailJobDetail() {
        String jobEmail = this.MAIL;
        validateJobEmailAddress(jobEmail);
        Long jobId = userService.createJobUserByNameLockByPessimisticWriteIfNotExistsAndGetId(EMAIL_SCHEDULER_JOB_IDENTITY, jobEmail, EMAIL_SCHEDULER_JOB_DESCRIPTION);
        return JobBuilder
                .newJob(EmailJob.class)
                .withIdentity(initializeAndLogBeanName(EMAIL_SCHEDULER_JOB_IDENTITY))
                .withDescription(EMAIL_SCHEDULER_JOB_DESCRIPTION)
                .usingJobData(JOB_ID, jobId)
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    public Trigger mailTrigger(JobDetail mailJobDetail) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity(initializeAndLogBeanName(EMAIL_SCHEDULER_JOB_IDENTITY))
                .withDescription(EMAIL_SCHEDULER_JOB_DESCRIPTION)
                .forJob(mailJobDetail)
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(CRON_PATTERN_MAIL)
                        .inTimeZone(TimeZone.getTimeZone(TIME_ZONE_ID))
                ).build();
    }

    private void validateJobEmailAddress(String email) {
        if (email == null || !EmailValidator.getInstance().isValid(email) || email.isBlank()) {
            if (email == null) {
                throwInternalServerExceptionWithMessage("not be null");
            } else if (!EmailValidator.getInstance().isValid(email)) {
                throwInternalServerExceptionWithMessage("be a valid email address");
            }
            throwInternalServerExceptionWithMessage("not be blank");
        }
    }

    private void throwInternalServerExceptionWithMessage(String message) {
        throw new InternalServerException(String.format("%s -- NOTIFICATION_SCHEDULER_MAIL (plm.notification.scheduler.mail) must %s",
                ExceptionEnum.SCHEDULER_CONFIGURATION_EXCEPTION.getFullMessage(), message),
                new Throwable()
        );
    }

    private String initializeAndLogBeanName(String name) {
        logger.debug(String.format("Initializing %s...", name));
        return name;
    }


}
