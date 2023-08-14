package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.RefreshJwtConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class RefreshJWTService extends JWTService {

    public RefreshJWTService(RefreshJwtConfig refreshJwtConfig) {
        super(refreshJwtConfig);
    }

    @Override
    public @Nullable String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest) {
        return getTokenFromRequestHeader(httpServletRequest);
    }
}
