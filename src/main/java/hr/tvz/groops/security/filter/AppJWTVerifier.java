package hr.tvz.groops.security.filter;

import hr.tvz.groops.model.constants.JWTConstants;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.AuthenticationService;
import hr.tvz.groops.service.token.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component
public class AppJWTVerifier extends JWTVerifier {
    private final AuthenticationService authenticationService;

    @Autowired
    public AppJWTVerifier(AuthenticationService authenticationService, JWTService appJWTService) {
        super(appJWTService);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void handleTokenRequest(HttpServletRequest request, String requestToken) {
//        try {
//            Jws<Claims> claimsJws = getJwtService().getClaimsFromToken(requestToken);
//            Claims body = claimsJws.getBody();
//            String username = body.get(JWTConstants.USERNAME, String.class);
//            String email = body.get(JWTConstants.EMAIL, String.class);
//            var roles = (Collection<String>) body.get(JWTConstants.ROLES);
//
//            Authentication authentication = new GroopsUserDataToken(username, email, null, getRoles(roles));
//            setAuthentication(authentication);
//
//        } catch (JwtException e) {
//            throw new IllegalStateException(String.format(invalidTokenErrorMessage, requestToken));
//        }
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) throws ServletException {
        return authenticationService.andMatchesAny(request);
    }
}
