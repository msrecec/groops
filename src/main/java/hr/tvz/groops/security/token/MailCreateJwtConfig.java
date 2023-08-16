package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.mail.create")
public class MailCreateJwtConfig extends JwtConfig {
    public MailCreateJwtConfig() {
        super();
    }
}
