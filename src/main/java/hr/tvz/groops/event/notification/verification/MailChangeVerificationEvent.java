package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.service.verification.VerificationVisitorService;
import org.jetbrains.annotations.NotNull;

public class MailChangeVerificationEvent extends VerificationEvent {
    public MailChangeVerificationEvent(Object source, @NotNull Long verificationId, @NotNull Long userId) {
        super(source, verificationId, userId);
    }

    @Override
    public void accept(VerificationVisitorService verificationVisitorService) {
        verificationVisitorService.visitMailChangeVerification(this);
    }
}
