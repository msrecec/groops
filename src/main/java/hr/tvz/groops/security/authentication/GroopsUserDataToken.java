package hr.tvz.groops.security.authentication;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class GroopsUserDataToken extends UsernamePasswordAuthenticationToken {
    @NotNull
    private final Long id;
    @NotNull
    private final String email;

    public GroopsUserDataToken(@NotNull Object username, Object credentials, @NotNull Long id,  @NotNull String email) {
        super(username, credentials);
        this.id = id;
        this.email = email;
    }

    public GroopsUserDataToken(@NotNull Object username, Object credentials, Collection<? extends GrantedAuthority> authorities, @NotNull Long id, @NotNull String email) {
        super(username, credentials, authorities);
        this.id = id;
        this.email = email;
    }
}
