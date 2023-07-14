package hr.tvz.groops.exception;

import org.springframework.http.HttpStatus;

public abstract class ExternalServiceException extends RuntimeException {

    private final HttpStatus httpStatus;
    private String responseBody;
    private String clientMessage = ExceptionEnum.RUNTIME_EXCEPTION.getShortMessage();

    public ExternalServiceException(String message, HttpStatus httpStatus, String clientMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.clientMessage = clientMessage;
    }

    public ExternalServiceException(String message, String responseBody, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public ExternalServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
