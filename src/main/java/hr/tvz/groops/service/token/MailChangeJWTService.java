package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.MailChangeJwtConfig;
import org.springframework.stereotype.Service;

@Service
public class MailChangeJWTService extends VerificationJWTService {

    public MailChangeJWTService(MailChangeJwtConfig mailChangeJwtConfig) {
        super(mailChangeJwtConfig);
    }
}
