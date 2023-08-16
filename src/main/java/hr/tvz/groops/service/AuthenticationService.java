package hr.tvz.groops.service;

import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private final List<RequestMatcher> andRequestMatchers;

    @Autowired
    public AuthenticationService(List<RequestMatcher> andRequestMatchers) {
        this.andRequestMatchers = andRequestMatchers;
    }

    @NotNull
    public String getCurrentLoggedInUserUsername() {
//        return SecurityUtil.getCurrentLoggedInUserUsername();
        return "test";
    }

    public boolean andMatchesAny(HttpServletRequest request) {
        return anyAndMatches(request);
    }

    private boolean anyAndMatches(HttpServletRequest request) {
        for (RequestMatcher andRequestMatcher : andRequestMatchers) {
            if (andRequestMatcher.matches(request)) {
                return true;
            }
        }
        return false;
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
