package hr.tvz.groops.service.impl;

import hr.tvz.groops.model.enums.PermissionEnum;
import hr.tvz.groops.model.enums.RoleEnum;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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
        return new GroopsUserDataToken("test", rolesAndPermissions, 1L);
    }

}
