package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hr.tvz.groops.constants.URLConstants.PASSWORD_CHANGE_TEMPLATE_URL;
import static hr.tvz.groops.constants.URLConstants.PASSWORD_FORGOT_TEMPLATE_URL;

@Component
public class PasswordChangeJWTVerifier extends VerificationJWTVerifier {

    @Autowired
    public PasswordChangeJWTVerifier(JWTService passwordChangeJWTService) {
        super(passwordChangeJWTService, PASSWORD_CHANGE_TEMPLATE_URL);
    }
}
