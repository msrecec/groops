package hr.tvz.groops.exception.csv.testDeviceLogs;

public class NoSerialException extends InvalidValueException {
    public NoSerialException() {
        super();
    }

    public NoSerialException(String message) {
        super(message);
    }

    public NoSerialException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSerialException(Throwable cause) {
        super(cause);
    }

    public NoSerialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
