package hr.tvz.groops.exception.csv.testDeviceLogs;

import hr.triplus.plm.exception.csv.CSVProcessingException;

public class NonExistingKeyException extends CSVProcessingException {
    public NonExistingKeyException() {
        super();
    }

    public NonExistingKeyException(String message) {
        super(message);
    }

    public NonExistingKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistingKeyException(Throwable cause) {
        super(cause);
    }

    public NonExistingKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
