package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.password.forgot")
public class PasswordForgotJwtConfig extends JwtConfig {
    public PasswordForgotJwtConfig() {
        super();
    }
}
