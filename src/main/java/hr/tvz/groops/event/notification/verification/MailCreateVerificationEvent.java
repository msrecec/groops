package hr.tvz.groops.event.notification.verification;

import hr.tvz.groops.service.verification.VerificationVisitorService;

public class MailCreateVerificationEvent extends MailVerificationEvent {
    public MailCreateVerificationEvent(Object source, Long verificationId) {
        super(source, verificationId);
    }

    @Override
    public void accept(VerificationVisitorService verificationVisitorService) {
        verificationVisitorService.visitMailCreateVerification(this);
    }
}
