package hr.tvz.groops.exception.csv.testDeviceLogs;

public class NoDateTimeException extends InvalidValueException {
    public NoDateTimeException() {
        super();
    }

    public NoDateTimeException(String message) {
        super(message);
    }

    public NoDateTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDateTimeException(Throwable cause) {
        super(cause);
    }

    public NoDateTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
