package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.MailCreateJwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailCreateJWTService extends VerificationJWTService {
    @Autowired
    public MailCreateJWTService(MailCreateJwtConfig mailCreateJwtConfig) {
        super(mailCreateJwtConfig);
    }
}
