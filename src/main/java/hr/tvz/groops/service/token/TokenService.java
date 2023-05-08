package hr.tvz.groops.service.token;

import hr.tvz.groops.dto.response.JWTDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface TokenService {

    JWTDto getToken(@NotNull Long id, @NotNull String username, @NotNull String... roles);

    String generateTokenBase64(@NotNull Long id, @NotNull String username, @NotNull String... roles);

    String generateToken(@NotNull Long id, @NotNull String username, @NotNull Collection<?> roles);

    Jws<Claims> getClaimsFromToken(@NotNull String token);

    @Nullable
    String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest);
    @Nullable
    String getResponseHeader();

    String getTokenFromRequestParameter(@NotNull HttpServletRequest httpServletRequest);

    String getTokenFromRequestHeader(@NotNull HttpServletRequest httpServletRequest);

    boolean requestTokenHasInvalidFormat(@NotNull String requestToken);

    @Nullable
    String getParameterName();

    @Nullable
    String getCookieName();

}
