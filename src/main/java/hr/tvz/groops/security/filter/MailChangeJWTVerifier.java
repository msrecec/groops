package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hr.tvz.groops.constants.URLConstants.MAIL_CHANGE_TEMPLATE_URL;


@Component
public class MailChangeJWTVerifier extends VerificationJWTVerifier {

    @Autowired
    public MailChangeJWTVerifier(JWTService mailChangeJWTService) {
        super(mailChangeJWTService, MAIL_CHANGE_TEMPLATE_URL);
    }
}
