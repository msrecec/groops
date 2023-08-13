package hr.tvz.groops.jobs.config;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;

public class SingletonJobFactory implements JobFactory {

    private final ApplicationContext applicationContext;

    public SingletonJobFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        Class<?> jobClass = bundle.getJobDetail().getJobClass();
        return (Job) applicationContext.getBean(jobClass);
    }
}
