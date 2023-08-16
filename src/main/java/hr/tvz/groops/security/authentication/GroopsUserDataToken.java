package hr.tvz.groops.security.authentication;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class GroopsUserDataToken extends UsernamePasswordAuthenticationToken {
    private final String username;
    @NotNull
    private final String email;

    public GroopsUserDataToken(@NotNull String username, Object credentials, Collection<? extends GrantedAuthority> authorities, @NotNull String email) {
        super(username, credentials, authorities);
        this.username = username;
        this.email = email;
    }
}
