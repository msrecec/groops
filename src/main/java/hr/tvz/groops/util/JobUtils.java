package hr.tvz.groops.util;

import org.quartz.JobExecutionContext;
import static hr.tvz.groops.jobs.config.SchedulerConfig.MAIL_KEY;

public class JobUtils {
    public static String getJobName(JobExecutionContext jobExecutionContext) {
        return jobExecutionContext.getJobDetail().getKey().getName();
    }
    public static String getJobEmail(JobExecutionContext jobExecutionContext) {
        return jobExecutionContext.getJobDetail().getJobDataMap().getString(MAIL_KEY);
    }

    public static Long getLongValueByKey(JobExecutionContext jobExecutionContext, String key) {
        return jobExecutionContext.getJobDetail().getJobDataMap().getLong(key);
    }

}

