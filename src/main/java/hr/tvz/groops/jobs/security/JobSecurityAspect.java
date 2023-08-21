package hr.tvz.groops.jobs.security;

import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.util.JobUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class JobSecurityAspect {
    private static final Logger logger = LoggerFactory.getLogger(JobSecurityAspect.class);

    @Before("@annotation(hr.tvz.groops.jobs.annotation.JobSecurityContext)")
    public void beforeJobExecution(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof JobExecutionContext) {
                JobExecutionContext jobExecutionContext = (JobExecutionContext) arg;
                String jobName = JobUtils.getJobName(jobExecutionContext);
                Long jobId = JobUtils.getJobId(jobExecutionContext);
                if (jobName == null) {
                    logger.debug("Cant set security context for job with no name...");
                    return;
                }
                logger.debug(String.format("Initializing security context before job with name: %s executes...", jobName));
                setSecurityContext(jobName, jobId);
                return;
            }
        }
    }

    @After("@annotation(hr.tvz.groops.jobs.annotation.JobSecurityContext)")
    public void afterJobExecution(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof JobExecutionContext) {
                JobExecutionContext jobExecutionContext = (JobExecutionContext) arg;
                String jobName = JobUtils.getJobName(jobExecutionContext);
                if (jobName == null) {
                    logger.debug("Cant clear security context for job with no name...");
                    return;
                }
                logger.debug(String.format("Clearing security context after job with name: %s executes...", jobName));
                clearSecurityContext(jobName);
                return;
            }
        }
    }

    private void setSecurityContext(String jobName, Long jobId) {
        if (SecurityContextHolder.getContext() == null) {
            logger.debug(String.format("No security context, skipping setting authentication to job with name: %s...", jobName));
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(getAuthenticationToken(jobName, jobId));
    }

    private Authentication getAuthenticationToken(String jobName, Long jobId) {
        return new GroopsUserDataToken(jobId, jobName, null, null);
    }

    private void clearSecurityContext(String jobName) {
        if (SecurityContextHolder.getContext() == null) {
            logger.debug(String.format("No security context, skipping setting authentication to job with name: %s...", jobName));
            return;
        }
        SecurityContextHolder.clearContext();
    }

}
