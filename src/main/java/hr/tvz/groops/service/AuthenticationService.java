package hr.tvz.groops.service;

import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @NotNull
    public String getCurrentLoggedInUserUsername() {
//        return SecurityUtil.getCurrentLoggedInUserUsername();
        return "test";
    }

    @NotNull
    public GroopsUserDataToken getCurrentLoggedInUser() {
//        return SecurityUtil.getCurrentLoggedInUser();
        Set<String> rolesAndPermissions = new HashSet<>();
        return new GroopsUserDataToken("test", null, getRoles(rolesAndPermissions), 1L, "test@mail.com");
    }

    private Set<SimpleGrantedAuthority> getRoles(Collection<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
