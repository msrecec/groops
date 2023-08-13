package hr.tvz.groops.jobs.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    public static final String MAIL_KEY = "mail";
    private final String APP_MAIL;
    private final ApplicationContext applicationContext;

    @Autowired
    public SchedulerConfig(ApplicationContext applicationContext, @Value("${spring.mail.username}") String APP_MAIL) {
        this.applicationContext = applicationContext;
        this.APP_MAIL = APP_MAIL;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.setJobFactory(new SingletonJobFactory(applicationContext));
        return scheduler;
    }



}
