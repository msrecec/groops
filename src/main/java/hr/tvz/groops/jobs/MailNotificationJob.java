package hr.tvz.groops.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class MailNotificationJob {
}
