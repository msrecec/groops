package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.JwtConfig;

public abstract class VerificationJWTService extends JWTService {
    public VerificationJWTService(JwtConfig jwtConfig) {
        super(jwtConfig);
    }
}
