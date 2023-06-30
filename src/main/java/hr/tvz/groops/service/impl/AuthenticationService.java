package hr.tvz.groops.service.impl;

import hr.tvz.groops.utils.SecurityUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @NotNull
    public String getCurrentLoggedInUserUsername() {
        return SecurityUtil.getCurrentLoggedInUserUsername();
    }
}
