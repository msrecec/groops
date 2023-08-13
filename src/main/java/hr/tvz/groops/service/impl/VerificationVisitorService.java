package hr.tvz.groops.service.impl;

import hr.tvz.groops.event.notification.verification.MailChangeVerificationEvent;
import hr.tvz.groops.event.notification.verification.MailVerificationEvent;
import hr.tvz.groops.event.notification.verification.PasswordChangeVerificationEvent;
import hr.tvz.groops.model.constants.Constants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationVisitorService {
    @Transactional(timeout = Constants.DEFAULT_TIMEOUT)
    public void visitMailCreateVerification(MailVerificationEvent mailVerificationEvent) {
        // todo implement
    }
    @Transactional(timeout = Constants.DEFAULT_TIMEOUT)
    public void visitMailChangeVerification(MailChangeVerificationEvent mailChangeVerificationEvent) {
        // todo implement
    }
    @Transactional(timeout = Constants.DEFAULT_TIMEOUT)
    public void visitPasswordVerification(PasswordChangeVerificationEvent passwordChangeVerificationEvent) {
        // todo implement
    }
}
