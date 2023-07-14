package hr.tvz.groops.exception.csv;

public class CSVProcessingException extends RuntimeException {
    public CSVProcessingException() {
    }

    public CSVProcessingException(String message) {
        super(message);
    }

    public CSVProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CSVProcessingException(Throwable cause) {
        super(cause);
    }

    public CSVProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
