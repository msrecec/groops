package hr.tvz.groops.security.filter;

import hr.tvz.groops.model.constants.JWTConstants;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.token.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;

public abstract class VerificationJWTVerifier extends JWTVerifier {
    private final JWTService jwtService;
    private final RequestMatcher matcher;

    public VerificationJWTVerifier(JWTService jwtService, String path) {
        super(jwtService);
        this.jwtService = jwtService;
        this.matcher = getRequestMatcher(NegatedRequestMatcher::new, path);
    }

    @Override
    protected void handleTokenRequest(HttpServletRequest request, String requestToken) throws IllegalStateException {
        try {
            Jws<Claims> claimsJws = jwtService.getClaimsFromToken(requestToken);
            Claims body = claimsJws.getBody();
            String username = body.get(JWTConstants.USERNAME, String.class);
            String email = body.get(JWTConstants.EMAIL, String.class);
            var roles = (Collection<String>) body.get(JWTConstants.ROLES);
            Set<SimpleGrantedAuthority> authorities = getRoles(roles);

            Authentication authentication = new GroopsUserDataToken(username, null, authorities, email);
            setAuthentication(authentication);

        } catch (JwtException e) {
            throw new IllegalStateException(String.format(invalidTokenErrorMessage, requestToken));
        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        return this.matcher.matches(request);
    }
}
