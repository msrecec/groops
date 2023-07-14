package hr.tvz.groops.utils;

import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    @NotNull
    public static String getCurrentLoggedInUserUsername() {

        // todo check if an exception should be thrown in case there is no username

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return "no-authentication";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        if (principal == null) {
            logger.warn("No principal in security context");
            return "no-principal";
        }

        return principal.toString();
    }

    @NotNull
    public static GroopsUserDataToken getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new InternalServerException("No authentication", new Throwable());
        }

        if (!(authentication instanceof GroopsUserDataToken)) {
            throw new InternalServerException("Invalid authentication type", new Throwable());
        }

        return (GroopsUserDataToken) authentication;
    }

}
