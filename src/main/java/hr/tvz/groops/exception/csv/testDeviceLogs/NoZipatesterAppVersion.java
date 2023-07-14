package hr.tvz.groops.exception.csv.testDeviceLogs;

public class NoZipatesterAppVersion extends InvalidValueException {
    public NoZipatesterAppVersion() {
        super();
    }

    public NoZipatesterAppVersion(String message) {
        super(message);
    }

    public NoZipatesterAppVersion(String message, Throwable cause) {
        super(message, cause);
    }

    public NoZipatesterAppVersion(Throwable cause) {
        super(cause);
    }

    public NoZipatesterAppVersion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
