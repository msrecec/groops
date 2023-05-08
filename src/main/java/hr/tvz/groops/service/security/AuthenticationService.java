package hr.tvz.groops.service.security;

import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import hr.tvz.groops.util.SecurityUtil;
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
        return SecurityUtil.getCurrentLoggedInUserUsername();
    }

    @NotNull
    public Long getCurrentLoggedInUserId() {
        return SecurityUtil.getCurrentLoggedInUserId();
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
        return  SecurityUtil.getCurrentLoggedInUser();
    }

    private Set<SimpleGrantedAuthority> getRoles(Collection<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

}
