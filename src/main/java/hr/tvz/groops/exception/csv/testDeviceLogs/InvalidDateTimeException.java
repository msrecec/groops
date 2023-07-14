package hr.tvz.groops.exception.csv.testDeviceLogs;

public class InvalidDateTimeException extends InvalidValueException {
    public InvalidDateTimeException() {
        super();
    }

    public InvalidDateTimeException(String message) {
        super(message);
    }

    public InvalidDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDateTimeException(Throwable cause) {
        super(cause);
    }

    public InvalidDateTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
