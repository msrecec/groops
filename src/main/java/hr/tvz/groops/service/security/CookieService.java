package hr.tvz.groops.service.security;

import javax.servlet.http.HttpServletResponse;

public interface CookieService {
    void setResponseCookie(HttpServletResponse httpServletResponse, String tokenBase64);

    void unsetResponseCookie(HttpServletResponse httpServletResponse);
}
