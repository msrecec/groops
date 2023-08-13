package hr.tvz.groops.event.mail;

import org.springframework.context.ApplicationEvent;

public class MailEvent extends ApplicationEvent {
    private final Long senderId;
    private final Long recipientId;
    private final Long mailMessageId;

    public MailEvent(Object source, Long senderId, Long recipientId, Long mailMessageId) {
        super(source);
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.mailMessageId = mailMessageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public Long getMailMessageId() {
        return mailMessageId;
    }
}
