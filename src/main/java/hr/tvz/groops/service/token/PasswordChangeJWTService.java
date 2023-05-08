package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.PasswordChangeJwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordChangeJWTService extends VerificationJWTService {

    @Autowired
    public PasswordChangeJWTService(PasswordChangeJwtConfig passwordChangeJwtConfig) {
        super(passwordChangeJwtConfig);
    }

}
