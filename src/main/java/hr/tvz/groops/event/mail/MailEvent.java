package hr.tvz.groops.event.mail;

import org.springframework.context.ApplicationEvent;

public class MailEvent extends ApplicationEvent {
    private final Long mailId;

    public MailEvent(Object source, Long mailId) {
        super(source);
        this.mailId = mailId;
    }

    public Long getMailId() {
        return mailId;
    }
}
