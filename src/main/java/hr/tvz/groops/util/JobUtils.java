package hr.tvz.groops.util;

import org.quartz.JobExecutionContext;

import static hr.tvz.groops.jobs.config.SchedulerConfig.JOB_ID;

public class JobUtils {
    public static String getJobName(JobExecutionContext jobExecutionContext) {
        return jobExecutionContext.getJobDetail().getKey().getName();
    }

    public static Long getJobId(JobExecutionContext jobExecutionContext) {
        return getLongValueByKey(jobExecutionContext, JOB_ID);
    }

    public static Long getLongValueByKey(JobExecutionContext jobExecutionContext, String key) {
        return jobExecutionContext.getJobDetail().getJobDataMap().getLong(key);
    }

}

