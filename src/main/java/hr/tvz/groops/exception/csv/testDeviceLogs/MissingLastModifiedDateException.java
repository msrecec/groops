package hr.tvz.groops.exception.csv.testDeviceLogs;

public class MissingLastModifiedDateException extends NoValuesException {
    public MissingLastModifiedDateException() {
        super();
    }

    public MissingLastModifiedDateException(String message) {
        super(message);
    }

    public MissingLastModifiedDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingLastModifiedDateException(Throwable cause) {
        super(cause);
    }

    public MissingLastModifiedDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
