package hr.tvz.groops.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.refresh")
public class RefreshJwtConfig extends JwtConfig {
    public RefreshJwtConfig() {
        super();
    }
}
