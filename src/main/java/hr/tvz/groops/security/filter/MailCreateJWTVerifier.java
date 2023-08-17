package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hr.tvz.groops.constants.URLConstants.MAIL_CREATE_TEMPLATE_URL;

@Component
public class MailCreateJWTVerifier extends VerificationJWTVerifier {

    @Autowired
    public MailCreateJWTVerifier(JWTService mailCreateJWTService) {
        super(mailCreateJWTService, MAIL_CREATE_TEMPLATE_URL);
    }
}
