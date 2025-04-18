package com.yuce.chat.assistant.exception;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceStatus> globalExceptionHandler(Exception ex, WebRequest request) {
        return handleException(ex, request, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ServiceStatus> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ServiceStatus> handleException(Exception ex, WebRequest request, HttpStatus httpStatus) {
        log.error("An exception occurred", ex);

        ServiceStatus apiError = new ServiceStatus();
        apiError.setMessage(ex.getMessage());
        apiError.setStatusCode(httpStatus.value());
        apiError.setErrorDescription(getErrorDescription(ex));

        return new ResponseEntity<>(apiError, httpStatus);
    }

    private String getErrorDescription(Exception ex) {
        if (ex instanceof ConstraintViolationException) {
            return "Validation failed";
        } else if (ex instanceof MethodArgumentNotValidException) {
            return "Invalid request arguments";
        } else if (ex instanceof TypeMismatchException) {
            return "Invalid request type";
        }
        return "An unexpected error occurred";
    }
}