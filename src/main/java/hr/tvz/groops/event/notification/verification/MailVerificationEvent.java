package hr.tvz.groops.event.notification.verification;

public abstract class MailVerificationEvent extends VerificationEvent {
    public MailVerificationEvent(Object source, Long verificationId) {
        super(source, verificationId);
    }
}
