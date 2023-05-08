package hr.tvz.groops.security.filter;

import com.google.common.base.Strings;
import hr.tvz.groops.constants.JWTConstants;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.service.token.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class JWTVerifier extends OncePerRequestFilter {
    protected static final String invalidTokenErrorMessage = "Token %s cannot be trusted";
    protected final JWTService jwtService;

    public JWTVerifier(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().contains("authentication/login") ||
                request.getRequestURI().contains("authentication/logout") ||
                request.getRequestURI().contains("users/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestToken = jwtService.getTokenFromRequest(request);
        if (Strings.isNullOrEmpty(requestToken) || jwtService.requestTokenHasInvalidFormat(requestToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        handleTokenRequest(request, requestToken);
        filterChain.doFilter(request, response);
    }

    protected void handleTokenRequest(HttpServletRequest request, String requestToken) throws IllegalStateException {
        try {
            Jws<Claims> claimsJws = jwtService.getClaimsFromToken(requestToken);
            Claims body = claimsJws.getBody();
            Long id = body.get(JWTConstants.ID, Long.class);
            String username = body.get(JWTConstants.USERNAME, String.class);
            var roles = (Collection<String>) body.get(JWTConstants.ROLES);
            Set<SimpleGrantedAuthority> authorities = getRoles(roles);

            Authentication authentication = new GroopsUserDataToken(id, username, null, authorities);
            setAuthentication(authentication);

        } catch (JwtException e) {
            throw new IllegalStateException(String.format(invalidTokenErrorMessage, requestToken));
        }
    }

    protected Set<SimpleGrantedAuthority> getRoles(Collection<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    protected void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected RequestMatcher getRequestMatcher(Function<RequestMatcher, RequestMatcher> getRequestMatcher, String path) {
        return getRequestMatcher.apply(new AntPathRequestMatcher(path));
    }
}
