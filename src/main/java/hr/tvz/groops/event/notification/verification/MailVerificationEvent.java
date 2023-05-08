package hr.tvz.groops.event.notification.verification;

import org.jetbrains.annotations.NotNull;

public abstract class MailVerificationEvent extends VerificationEvent {
    public MailVerificationEvent(Object source, @NotNull Long verificationId, @NotNull Long userId) {
        super(source, verificationId, userId);
    }
}
