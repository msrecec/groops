package hr.tvz.groops.security.filter;

import hr.tvz.groops.service.token.JWTService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public abstract class VerificationJWTVerifier extends JWTVerifier {
    private final RequestMatcher matcher;

    public VerificationJWTVerifier(JWTService jwtService, String path) {
        super(jwtService);
        this.matcher = getRequestMatcher(NegatedRequestMatcher::new, path);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        return this.matcher.matches(request);
    }
}
