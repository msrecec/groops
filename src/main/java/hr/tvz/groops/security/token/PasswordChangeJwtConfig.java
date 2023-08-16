package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.password.change")
public class PasswordChangeJwtConfig extends JwtConfig {
    public PasswordChangeJwtConfig() {
        super();
    }
}
