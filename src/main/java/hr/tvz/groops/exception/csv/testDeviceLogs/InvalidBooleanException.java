package hr.tvz.groops.exception.csv.testDeviceLogs;

public class InvalidBooleanException extends InvalidValueException {
    public InvalidBooleanException() {
        super();
    }

    public InvalidBooleanException(String message) {
        super(message);
    }

    public InvalidBooleanException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBooleanException(Throwable cause) {
        super(cause);
    }

    public InvalidBooleanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
