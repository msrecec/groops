package hr.tvz.groops.service.token;

import hr.tvz.groops.security.token.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;

@Getter
public abstract class JWTService implements TokenService {
    private final JwtConfig jwtConfig;
    private final SecretKeySpec secretKey;
    private final SignatureAlgorithm algo = SignatureAlgorithm.HS256;

    public JWTService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        secretKey = new SecretKeySpec(jwtConfig.getSecretKey(), algo.getJcaName());
    }

    @Override
    public String generateTokenBase64(@NotNull String username, @NotNull String... roles) {
        return toBase64(getToken(username, new ArrayList<>(Arrays.asList(roles)), jwtConfig.getTokenExpirationAfterSeconds()));
    }

    @Override
    public String generateToken(@NotNull String username, @NotNull Collection<?> roles) {
        return getToken(username, roles, jwtConfig.getTokenExpirationAfterSeconds());
    }

    @Override
    public Jws<Claims> getClaimsFromToken(@NotNull String token) {
        String parsedToken = token.replace("" + jwtConfig.getTokenPrefix(), "");
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(parsedToken);
    }

    private String getToken(@NotNull String username, @NotNull Collection<?> roles, Long seconds) {
        String token = Jwts.builder()
                .signWith(secretKey, algo)
                .setClaims(getClaims(username, roles))
                .setSubject(username)
                .setIssuer(jwtConfig.getIssuer())
                .setExpiration(getExp(seconds))
                .setIssuedAt(new Date())
                .compact();
        return "" + jwtConfig.getTokenPrefix() + token;
    }


    private Map<String, Object> getClaims(@NotNull String username, @NotNull Collection<?> roles) {
        return Map.of("u", username, "r", roles);
    }

    private Date getExp(Long seconds) {
        return Date.from(Instant.now().plusSeconds(seconds));
    }

    @Override
    public @Nullable String getTokenFromRequest(@NotNull HttpServletRequest httpServletRequest) {
        String token = getTokenFromRequestHeader(httpServletRequest);
        if (token != null) {
            return token;
        }
        String tokenB64 = getTokenFromRequestParameter(httpServletRequest);
        return tokenB64 != null ? getFromBase64(tokenB64) : null;
    }

    @Nullable
    protected String getCookieValueFromBase64(String cookieName, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return getFromBase64(cookie.getValue());
            }
        }
        return null;
    }

    protected boolean hasCookies(String cookieName, Cookie[] cookies) {
        return cookieName != null && cookies != null;
    }

    protected boolean hasParameter(HttpServletRequest httpServletRequest, String parameter) {
        return parameter != null && httpServletRequest.getParameter(parameter) != null;
    }

    protected String getParameterFromBase64(HttpServletRequest httpServletRequest, String parameter) {
        return getFromBase64(httpServletRequest.getParameter(parameter));
    }


    @Override
    @Nullable
    public String getTokenFromRequestHeader(@NotNull HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(this.jwtConfig.getHeaderName());
    }

    @Override
    @Nullable
    public String getTokenFromRequestParameter(@NotNull HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter(this.jwtConfig.getParameterName());
    }

    private String getFromBase64(String value) {
        return new String(Base64.getDecoder().decode(value));
    }

    private String toBase64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    @Override
    public boolean requestTokenHasInvalidFormat(@NotNull String requestToken) {
        return jwtConfig.getTokenPrefix() != null && !requestToken.startsWith(jwtConfig.getTokenPrefix());
    }

    @Override
    public String getParameterName() {
        return jwtConfig.getParameterName();
    }

    @Override
    public String getCookieName() {
        return jwtConfig.getCookieName();
    }
}
