package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hr.tvz.groops.constants.URLConstants.PASSWORD_FORGOT_TEMPLATE_URL;

@Component
public class PasswordForgotJWTVerifier extends VerificationJWTVerifier {
    @Autowired
    public PasswordForgotJWTVerifier(JWTService passwordForgotJWTService) {
        super(passwordForgotJWTService, PASSWORD_FORGOT_TEMPLATE_URL);
    }
}
