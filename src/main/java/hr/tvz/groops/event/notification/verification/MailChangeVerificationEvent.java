package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.service.impl.VerificationVisitorService;

public class MailChangeVerificationEvent extends MailVerificationEvent {
    public MailChangeVerificationEvent(Object source, Long verificationId) {
        super(source, verificationId);
    }

    @Override
    public void accept(VerificationVisitorService verificationVisitorService) {
        verificationVisitorService.visitMailChangeVerification(this);
    }
}
