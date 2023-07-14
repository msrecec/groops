package hr.tvz.groops.exception.csv.testDeviceLogs;

public class InvalidIntegerException extends InvalidValueException {
    public InvalidIntegerException() {
        super();
    }

    public InvalidIntegerException(String message) {
        super(message);
    }

    public InvalidIntegerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIntegerException(Throwable cause) {
        super(cause);
    }

    public InvalidIntegerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
