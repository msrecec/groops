package hr.tvz.groops.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface TokenService {

    String generateTokenBase64(@NotNull String username, @NotNull String email, @NotNull String... roles);

    String generateTokenBase64(@NotNull String username, @NotNull String email, @NotNull Long subscriptionId, @NotNull String... roles);

    String generateToken(@NotNull String username, @NotNull String email, @NotNull Collection<?> roles);

    Jws<Claims> getClaimsFromToken(@NotNull String token);

    @Nullable
    String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest);

    String getTokenFromRequestParameter(@NotNull HttpServletRequest httpServletRequest);

    String getTokenFromRequestHeader(@NotNull HttpServletRequest httpServletRequest);

    boolean requestTokenHasInvalidFormat(@NotNull String requestToken);

    @Nullable
    String getParameterName();

    @Nullable
    String getCookieName();

}
