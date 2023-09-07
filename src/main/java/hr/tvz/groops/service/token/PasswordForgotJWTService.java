package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.PasswordForgotJwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordForgotJWTService extends VerificationJWTService {

    @Autowired
    public PasswordForgotJWTService(PasswordForgotJwtConfig passwordForgotJwtConfig) {
        super(passwordForgotJwtConfig);
    }

}
