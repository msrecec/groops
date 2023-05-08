package hr.tvz.groops.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import hr.tvz.groops.dto.response.CausedErrorDto;
import hr.tvz.groops.dto.response.Error409Dto;
import hr.tvz.groops.dto.response.ErrorDto;
import hr.tvz.groops.dto.response.ExternalServiceErrorDto;
import hr.tvz.groops.exception.ExceptionEnum;
import hr.tvz.groops.exception.ExternalServiceException;
import hr.tvz.groops.exception.InternalServerException;

import javax.persistence.EntityNotFoundException;

import hr.tvz.groops.exception.UnauthorizedException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

public abstract class ControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(ControllerBase.class);


    @ExceptionHandler(AmazonS3Exception.class)
    @ResponseBody
    public ResponseEntity<CausedErrorDto> amazonException(AmazonS3Exception ex) {
        logger.error(ExceptionEnum.S3_EXCEPTION.getFullMessage(), ex);
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(CausedErrorDto.builder()
                        .success(false)
                        .message(ExceptionEnum.S3_EXCEPTION.getShortMessage())
                        .cause(ex.getMessage())
                        .status(ex.getStatusCode())
                        .build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto entityNotFoundException(EntityNotFoundException ex) {
        logger.error(ExceptionEnum.ENTITY_NOT_FOUND_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(LockAcquisitionException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public CausedErrorDto lockAcquisitionException(LockAcquisitionException ex) {
        logger.error(ExceptionEnum.LOCK_ACQUISITION_EXCEPTION.getFullMessage(), ex);
        return CausedErrorDto.builder()
                .success(false)
                .cause(ex.getMessage())
                .message(ExceptionEnum.LOCK_ACQUISITION_EXCEPTION.getShortMessage())
                .status(HttpStatus.PRECONDITION_FAILED.value())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto illegalArgumentException(IllegalArgumentException ex) {
        logger.error(ExceptionEnum.ILLEGAL_ARGUMENT_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CausedErrorDto runtimeException(RuntimeException ex) {
        logger.error(ExceptionEnum.RUNTIME_EXCEPTION.getFullMessage(), ex);
        return CausedErrorDto.builder()
                .success(false)
                .cause(ex.getMessage())
                .message(ExceptionEnum.RUNTIME_EXCEPTION.getShortMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseBody
    public ResponseEntity<CausedErrorDto> internalServerException(InternalServerException ex) {
        logger.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(CausedErrorDto.builder()
                        .success(false)
                        .cause(ex.getCause().getMessage())
                        .message(ex.getClientMessage())
                        .status(ex.getHttpStatus().value())
                        .build());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseBody
    public ResponseEntity<ExternalServiceErrorDto> externalServiceException(ExternalServiceException ex) {
        logger.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ExternalServiceErrorDto.builder()
                        .success(false)
                        .message(ex.getClientMessage())
                        .serviceResponse(ex.getResponseBody())
                        .status(ex.getHttpStatus().value())
                        .build());
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto emptyResultDataAccessException(EmptyResultDataAccessException ex) {
        logger.error(ExceptionEnum.EMPTY_RESULT_DATA_ACCESS_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDto validationException(BindException ex) {
        logger.error(ExceptionEnum.BIND_EXCEPTION.getFullMessage(), ex);
        String message = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
        return new ErrorDto(false, message, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDto accessDeniedException(AccessDeniedException ex) {
        logger.error(ExceptionEnum.ACCESS_DENIED_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDto unauthorizedException(UnauthorizedException ex) {
        logger.error(ExceptionEnum.UNAUTHORIZED_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : ExceptionEnum.UNAUTHORIZED_EXCEPTION.getShortMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CausedErrorDto exceptionException(Exception ex) {
        logger.error(ExceptionEnum.EXCEPTION.getFullMessage(), ex);
        return CausedErrorDto.builder()
                .success(false)
                .cause(ex.getMessage())
                .message(ExceptionEnum.EXCEPTION.getShortMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorDto> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error(ExceptionEnum.DATA_INTEGRITY_VIOLATION_EXCEPTION.getFullMessage(), ex);
        String message = ex.getMostSpecificCause().getMessage();
        if (ex.getCause() instanceof ConstraintViolationException) {
            return new ResponseEntity<>(constraintViolationException((ConstraintViolationException) ex.getCause()), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new ErrorDto(false, message, HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Error409Dto constraintViolationException(ConstraintViolationException ex) {
        logger.error(ExceptionEnum.CONSTRAINT_VIOLATION_EXCEPTION.getFullMessage(), ex);
        ServerErrorMessage serverErrorMessage = null;
        String defaultMessage = null;
        if (ex.getCause() instanceof PSQLException) {
            serverErrorMessage = ((PSQLException) ex.getCause()).getServerErrorMessage();
        } else if (ex.getCause() != null) {
            defaultMessage = ex.getCause().getMessage();
        } else {
            defaultMessage = ex.getMessage();
        }
        String message = serverErrorMessage != null ? serverErrorMessage.getMessage() : defaultMessage != null ? defaultMessage : ExceptionEnum.CONSTRAINT_VIOLATION_EXCEPTION.getShortMessage();
        return new Error409Dto(false, ex.getConstraintName(), message, HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(JpaSystemException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorDto jpaSystemException(JpaSystemException ex) {
        logger.error(ExceptionEnum.JPA_SYSTEM_EXCEPTION.getFullMessage(), ex);
        return new ErrorDto(false, ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDto transactionSystemException(TransactionSystemException ex) {
        logger.error(ExceptionEnum.TRANSACTION_SYSTEM_EXCEPTION.getFullMessage(), ex);
        String message = ex.getMessage();
        String constraint = null;
        if (ex.getRootCause() instanceof javax.validation.ConstraintViolationException) {
            constraint = ((javax.validation.ConstraintViolationException) ex.getRootCause())
                    .getConstraintViolations().stream().map(
                            v -> v.getPropertyPath() + " " + v.getMessage()
                    ).collect(Collectors.toList()).toString();
            message = constraint;
        }
        return new Error409Dto(false, constraint, message, HttpStatus.CONFLICT.value());
    }
}