package hr.tvz.groops.exception;

import org.springframework.http.HttpStatus;

public class S3Exception extends ExternalServiceException {

    public S3Exception(String message, HttpStatus httpStatus, String clientMessage) {
        super(message, httpStatus, clientMessage);
    }
}
