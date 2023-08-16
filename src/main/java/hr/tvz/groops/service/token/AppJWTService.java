package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.AppJwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppJWTService extends JWTService {
    @Autowired
    public AppJWTService(AppJwtConfig appJwtConfig) {
        super(appJwtConfig);
    }
}
