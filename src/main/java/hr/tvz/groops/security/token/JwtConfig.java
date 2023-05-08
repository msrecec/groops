package hr.tvz.groops.security.token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.DatatypeConverter;

public abstract class JwtConfig {
    private String secretKeyBase64;
    private byte[] secretKey;
    private String tokenPrefix;
    private Long tokenExpirationAfterSeconds;
    private String issuer;
    private String headerName;
    private String cookieName;
    private String parameterName;

    public JwtConfig() {
    }

    public String getSecretKeyBase64() {
        return secretKeyBase64;
    }

    public void setSecretKeyBase64(@NotNull String secretKeyB64) {
        this.secretKey = DatatypeConverter.parseBase64Binary(secretKeyB64);
        this.secretKeyBase64 = secretKeyB64;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte @NotNull [] secretKey) {
        this.secretKey = secretKey;
    }

    @Nullable
    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(@Nullable String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Long getTokenExpirationAfterSeconds() {
        return tokenExpirationAfterSeconds;
    }

    public void setTokenExpirationAfterSeconds(@NotNull Long tokenExpirationAfterSeconds) {
        this.tokenExpirationAfterSeconds = tokenExpirationAfterSeconds;
    }

    public JwtConfig(String issuer) {
        this.issuer = issuer;
    }

    @Nullable
    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(@Nullable String issuer) {
        this.issuer = issuer;
    }

    @NotNull
    public String getHeaderName() {
        return this.headerName;
    }

    public void setHeaderName(@NotNull String header) {
        this.headerName = header;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookie) {
        this.cookieName = cookie;
    }

    @Nullable
    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(@Nullable String parameter) {
        this.parameterName = parameter;
    }
}
