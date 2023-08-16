package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.PasswordChangeJwtConfig;
import org.springframework.stereotype.Service;

@Service
public class PasswordChangeJWTService extends VerificationJWTService {

    public PasswordChangeJWTService(PasswordChangeJwtConfig passwordChangeJwtConfig) {
        super(passwordChangeJwtConfig);
    }

}
