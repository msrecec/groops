package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.AppJwtConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AppJWTService extends JWTService {
    @Autowired
    public AppJWTService(AppJwtConfig appJwtConfig) {
        super(appJwtConfig);
    }

    @Override
    public @Nullable String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest) {
        return getTokenFromRequestHeader(httpServletRequest);
    }
}
