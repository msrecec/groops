package hr.tvz.groops.security.filter;

import com.google.common.base.Strings;
import hr.tvz.groops.service.token.JWTService;
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
    private final JWTService jwtService;

    public JWTVerifier(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    abstract protected void handleTokenRequest(HttpServletRequest request, String requestToken) throws IllegalStateException;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String requestToken = jwtService.getTokenFromRequest(request);
        if (Strings.isNullOrEmpty(requestToken) || jwtService.requestTokenHasInvalidFormat(requestToken)) {
            filterChain.doFilter(request, response);
            return;
        }
        handleTokenRequest(request, requestToken);
        filterChain.doFilter(request, response);
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
