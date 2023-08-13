package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.event.notification.NotificationEvent;

public abstract class VerificationEvent extends NotificationEvent {
    private final Long userId;

    public VerificationEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
