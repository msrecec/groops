package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.service.verification.VerificationVisitorService;
import org.jetbrains.annotations.NotNull;

public class PasswordChangeVerificationEvent extends VerificationEvent {
    public PasswordChangeVerificationEvent(Object source, @NotNull Long verificationId, @NotNull Long userId) {
        super(source, verificationId, userId);
    }

    @Override
    public void accept(VerificationVisitorService verificationVisitorService) {
        verificationVisitorService.visitPasswordVerification(this);
    }
}
