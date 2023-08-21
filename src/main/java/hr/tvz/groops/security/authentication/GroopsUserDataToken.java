package hr.tvz.groops.security.authentication;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class GroopsUserDataToken extends UsernamePasswordAuthenticationToken {
    @NotNull
    private final Long userId;
    @NotNull
    private final String username;

    public GroopsUserDataToken(@NotNull Long userId, @NotNull String username, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(username, credentials, authorities);
        this.userId = userId;
        this.username = username;
    }
}
