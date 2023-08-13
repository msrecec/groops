package hr.tvz.groops.util;

import org.quartz.JobExecutionContext;

public class JobUtils {
    public static String getJobName(JobExecutionContext jobExecutionContext) {
        return jobExecutionContext.getJobDetail().getKey().getName();
    }

    public static Long getLongValueByKey(JobExecutionContext jobExecutionContext, String key) {
        return jobExecutionContext.getJobDetail().getJobDataMap().getLong(key);
    }

}

