package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.mail")
public class MailJwtConfig extends JwtConfig {
    public MailJwtConfig() {
        super();
    }
}
