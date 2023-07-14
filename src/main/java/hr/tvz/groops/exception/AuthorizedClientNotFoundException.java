package hr.tvz.groops.exception;

public class AuthorizedClientNotFoundException extends RuntimeException{

    public AuthorizedClientNotFoundException() {
        super();
    }

    public AuthorizedClientNotFoundException(String message) {
        super(message);
    }

    public AuthorizedClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizedClientNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AuthorizedClientNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
