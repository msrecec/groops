package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.event.notification.NotificationEvent;
import hr.tvz.groops.service.verification.VerificationVisitorService;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class VerificationEvent extends NotificationEvent {
    @NotNull
    private final Long verificationId;

    public VerificationEvent(Object source, @NotNull Long verificationId, @NotNull Long userId) {
        super(source, userId);
        this.verificationId = verificationId;
    }

    public abstract void accept(VerificationVisitorService verificationVisitorService);
}
