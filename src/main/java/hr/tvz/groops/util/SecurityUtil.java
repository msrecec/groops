package hr.tvz.groops.util;

import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static @NotNull String getCurrentLoggedInUserUsername() {

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

    public static @NotNull Long getCurrentLoggedInUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new InternalServerException("No authentication", new Throwable());
        }

        if (!(authentication instanceof GroopsUserDataToken)) {
            throw new InternalServerException("Invalid authentication type", new Throwable());
        }

        return ((GroopsUserDataToken) authentication).getUserId();
    }

    public static @NotNull GroopsUserDataToken getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new InternalServerException("No authentication", new Throwable());
        }

        if (!(authentication instanceof GroopsUserDataToken)) {
            throw new InternalServerException("Invalid authentication type", new Throwable());
        }

        return (GroopsUserDataToken) authentication;
    }

    /**
     * Source: <a href="https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/">How to validate a password</a>
     *
     * @param password
     * @return
     */
    public static boolean isValidPassword(String password) {
        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);

        // Return if the password
        // matched the ReGex
        return m.matches();
    }

    public static @NotNull Set<SimpleGrantedAuthority> getRoles(Collection<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }


    public static boolean hasAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication() != null;
    }

    public static boolean hasPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null;
    }

}
