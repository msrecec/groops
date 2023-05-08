package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hr.tvz.groops.constants.URLConstants.VERIFICATION_RESEND_URL;

@Component
public class VerificationResendJWTVerifier extends VerificationJWTVerifier {
    @Autowired
    public VerificationResendJWTVerifier(JWTService verificationResendJWTService) {
        super(verificationResendJWTService, VERIFICATION_RESEND_URL);
    }
}
