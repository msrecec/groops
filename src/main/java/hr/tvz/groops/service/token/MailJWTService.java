package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.MailJwtConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class MailJWTService extends JWTService {
    @Autowired
    public MailJWTService(MailJwtConfig mailJwtConfig) {
        super(mailJwtConfig);
    }

    @Override
    public @Nullable String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest) {
        if (hasParameter(httpServletRequest, super.getJwtConfig().getParameterName())) {
            return getParameterFromBase64(httpServletRequest, super.getJwtConfig().getParameterName());
        }

        if (hasCookies(super.getJwtConfig().getCookieName(), httpServletRequest.getCookies())) {
            return getCookieValueFromBase64(super.getJwtConfig().getCookieName(), httpServletRequest.getCookies());
        }
        return null;
    }
}
