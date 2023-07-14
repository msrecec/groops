package hr.tvz.groops.exception.csv.testDeviceLogs;

import hr.triplus.plm.exception.csv.CSVProcessingException;

public class NoKeyException extends CSVProcessingException {
    public NoKeyException() {
        super();
    }

    public NoKeyException(String message) {
        super(message);
    }

    public NoKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoKeyException(Throwable cause) {
        super(cause);
    }

    public NoKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
