package hr.tvz.groops.event.notification.verification;

public class PasswordVerificationEvent extends VerificationEvent {
    public PasswordVerificationEvent(Object source, Long userId) {
        super(source, userId);
    }
}
