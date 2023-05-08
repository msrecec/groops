package hr.tvz.groops.util;

import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.InternalServerException;
import hr.tvz.groops.security.authentication.GroopsUserDataToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public static void validatePassword(String password) {
        List<String> exceptionMessages = new ArrayList<>();
        boolean isValid = isValidHandler(password, exceptionMessages);
        if (isValid) {
            return;
        }
        logger.debug(ExceptionEnum.INVALID_PASSWORD_EXCEPTION.getFullMessage());
        throw new IllegalArgumentException(String.join("\n", exceptionMessages));
    }

    public static boolean isValid(String password, List<String> exceptionMessages) {
        return isValidHandler(password, exceptionMessages);
    }

    private static boolean isValidHandler(String password, List<String> exceptionMessages) {
        boolean isValid = true;
        if (password.length() > 15 || password.length() < 8) {
            exceptionMessages.add("Password must be less than 20 and more than 8 characters in length.");
            isValid = false;
        }
        String upperCaseChars = "(.*[A-Z].*)";
        if (!password.matches(upperCaseChars)) {
            exceptionMessages.add("Password must have at least one uppercase character");
            isValid = false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if (!password.matches(lowerCaseChars)) {
            exceptionMessages.add("Password must have at least one lowercase character");
            isValid = false;
        }
        String numbers = "(.*[0-9].*)";
        if (!password.matches(numbers)) {
            exceptionMessages.add("Password must have at least one number");
            isValid = false;
        }
        String specialChars = "(.*[@,#,$,%,!].*$)";
        if (!password.matches(specialChars)) {
            exceptionMessages.add("Password must have at least one special character among @#$%!");
            isValid = false;
        }
        return isValid;
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
