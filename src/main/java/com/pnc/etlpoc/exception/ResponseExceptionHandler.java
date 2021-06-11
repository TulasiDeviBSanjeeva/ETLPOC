package com.pnc.etlpoc.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global Exception Handler.
 */
@ControllerAdvice
@Slf4j
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError apiError = new ApiError(status.toString(), ex.getMessage(), ex.getClass().getSimpleName());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
    }

    @ExceptionHandler({FileParseException.class, JobParametersInvalidException.class})
    protected ResponseEntity<Object> requestParameterValidationException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(status.toString(), ex.getMessage(), ex.getClass().getSimpleName());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> globalExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError = new ApiError(status.toString(), ex.getMessage(), ex.getClass().getSimpleName());
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class ApiError {
        String status;
        String message;
        String exception;
    }

}
