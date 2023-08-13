package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.service.impl.VerificationVisitorService;

public class PasswordChangeVerificationEvent extends VerificationEvent {
    public PasswordChangeVerificationEvent(Object source, Long verificationId) {
        super(source, verificationId);
    }

    @Override
    public void accept(VerificationVisitorService verificationVisitorService) {
        verificationVisitorService.visitPasswordVerification(this);
    }
}
