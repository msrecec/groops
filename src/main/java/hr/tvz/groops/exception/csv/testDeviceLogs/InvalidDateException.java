package hr.tvz.groops.exception.csv.testDeviceLogs;

public class InvalidDateException extends InvalidValueException {
    public InvalidDateException() {
        super();
    }

    public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDateException(Throwable cause) {
        super(cause);
    }

    public InvalidDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
