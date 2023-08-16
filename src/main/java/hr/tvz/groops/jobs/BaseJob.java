package hr.tvz.groops.jobs;

import hr.tvz.groops.util.JobUtils;
import org.jetbrains.annotations.Nullable;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public abstract class BaseJob implements Job {

    protected String getJobName(JobExecutionContext jobExecutionContext) {
        return JobUtils.getJobName(jobExecutionContext);
    }

    @Nullable
    protected String getJobEmail(JobExecutionContext jobExecutionContext) {
        return JobUtils.getJobEmail(jobExecutionContext);

    }

    protected Long getStringValueByKey(JobExecutionContext jobExecutionContext, String key) {
        return JobUtils.getLongValueByKey(jobExecutionContext, key);
    }

    public abstract boolean isRunning();

}