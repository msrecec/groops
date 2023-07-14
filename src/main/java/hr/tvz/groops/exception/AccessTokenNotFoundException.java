package hr.tvz.groops.exception;

public class AccessTokenNotFoundException extends RuntimeException {

    public AccessTokenNotFoundException() {
        super();
    }

    public AccessTokenNotFoundException(String message) {
        super(message);
    }

    public AccessTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessTokenNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AccessTokenNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
