package hr.tvz.groops.event.notification.verification;

public class MailVerificationEvent extends VerificationEvent {
    public MailVerificationEvent(Object source, Long userId) {
        super(source, userId);
    }
}
