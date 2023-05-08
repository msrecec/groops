package hr.tvz.groops.service.security;

import hr.tvz.groops.service.token.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

import static hr.tvz.groops.constants.ProfileConstants.DEV;

@Service
public class AppCookieService implements CookieService {
    private final JWTService appJWTService;
    private final Environment environment;

    @Autowired
    public AppCookieService(JWTService appJWTService,
                            Environment environment) {
        this.appJWTService = appJWTService;
        this.environment = environment;
    }

    @Override
    public void setResponseCookie(HttpServletResponse httpServletResponse, String tokenBase64) {
        if (appJWTService.getCookieName() == null) {
            throw new IllegalArgumentException("Can't generate cookie for service that has no defined cookie name");
        }
        if (environment.acceptsProfiles(Profiles.of(DEV))) {
            ResponseCookie responseCookie = ResponseCookie.from(appJWTService.getCookieName(), tokenBase64)
                    .httpOnly(true)
                    .build();
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return;
        }
        ResponseCookie responseCookie = ResponseCookie.from(appJWTService.getCookieName(), tokenBase64)
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    @Override
    public void unsetResponseCookie(HttpServletResponse httpServletResponse) {
        if (appJWTService.getCookieName() == null) {
            throw new IllegalArgumentException("Can't generate cookie for service that has no defined cookie name");
        }
        if (environment.acceptsProfiles(Profiles.of(DEV))) {
            ResponseCookie responseCookie = ResponseCookie.from(appJWTService.getCookieName(), "")
                    .httpOnly(true)
                    .maxAge(0)
                    .build();
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            return;
        }
        ResponseCookie responseCookie = ResponseCookie.from(appJWTService.getCookieName(), "")
                .secure(true)
                .httpOnly(true)
                .sameSite("Strict")
                .maxAge(0)
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

}
