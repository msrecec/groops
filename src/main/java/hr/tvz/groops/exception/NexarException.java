package hr.tvz.groops.exception;

import org.springframework.http.HttpStatus;

public class NexarException extends JsonServiceException {

    public NexarException(HttpStatus httpStatus) {
        super(ExceptionEnum.NEXAR_EXCEPTION.getFullMessage(), httpStatus);
    }

    public NexarException(HttpStatus httpStatus, String responseBody) {
        super(ExceptionEnum.NEXAR_EXCEPTION.getFullMessage(), responseBody, httpStatus);
    }
}
