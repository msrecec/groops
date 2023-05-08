package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.mail.change")
public class MailChangeJwtConfig extends JwtConfig {
    public MailChangeJwtConfig() {
        super();
    }
}
