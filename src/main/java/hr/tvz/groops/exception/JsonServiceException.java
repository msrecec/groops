package hr.tvz.groops.exception;

import org.springframework.http.HttpStatus;

public abstract class JsonServiceException extends ExternalServiceException {

    public JsonServiceException(String fullMessage, HttpStatus httpStatus) {
        super(fullMessage, httpStatus);
    }


    public JsonServiceException(String fullMessage, String responseBody, HttpStatus httpStatus) {
        super(fullMessage, responseBody, httpStatus);
    }
}
