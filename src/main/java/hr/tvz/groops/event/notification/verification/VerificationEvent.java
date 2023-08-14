package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.event.notification.NotificationEvent;
import hr.tvz.groops.service.verification.VerificationVisitorService;

public abstract class VerificationEvent extends NotificationEvent {
    private final Long verificationId;

    public VerificationEvent(Object source, Long verificationId) {
        super(source);
        this.verificationId = verificationId;
    }

    public abstract void accept(VerificationVisitorService verificationVisitorService);

    public Long getUserId() {
        return verificationId;
    }
}
