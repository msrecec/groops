package hr.tvz.groops.exception.csv.testDeviceLogs;

import hr.triplus.plm.exception.csv.CSVProcessingException;

public class NoValuesException extends CSVProcessingException {
    public NoValuesException() {
        super();
    }

    public NoValuesException(String message) {
        super(message);
    }

    public NoValuesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoValuesException(Throwable cause) {
        super(cause);
    }

    public NoValuesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
