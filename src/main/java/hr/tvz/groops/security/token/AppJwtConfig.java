package hr.tvz.groops.security.token;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "groops.jwt.app")
public class AppJwtConfig extends JwtConfig {
    public AppJwtConfig() {
        super();
    }
}
