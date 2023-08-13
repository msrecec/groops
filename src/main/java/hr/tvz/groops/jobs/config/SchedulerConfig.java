package hr.tvz.groops.jobs.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    public static final String JOB_ID_KEY = "jobId";
    private final ApplicationContext applicationContext;

    @Autowired
    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        logger.trace("Initializing scheduler bean with singleton job factory...");
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.setJobFactory(new SingletonJobFactory(applicationContext));
        return scheduler;
    }



}
