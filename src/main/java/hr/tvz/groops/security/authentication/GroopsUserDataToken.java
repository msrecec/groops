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

    public GroopsUserDataToken(Object principal, Object credentials, @NotNull Long id) {
        super(principal, credentials);
        this.id = id;
    }

    public GroopsUserDataToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, @NotNull Long id) {
        super(principal, credentials, authorities);
        this.id = id;
    }
}
