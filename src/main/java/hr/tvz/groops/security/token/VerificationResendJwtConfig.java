package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.verification.resend")
public class VerificationResendJwtConfig extends JwtConfig {
    public VerificationResendJwtConfig() {
        super();
    }
}
