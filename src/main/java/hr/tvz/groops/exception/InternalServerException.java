package hr.tvz.groops.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private final String clientMessage = ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getShortMessage();

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(String message) {
        super(message, new Throwable());
    }

    public InternalServerException(Throwable cause) {
        super(ExceptionEnum.INTERNAL_SERVER_EXCEPTION.getFullMessage(), cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
