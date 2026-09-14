package com.imposter.game.exception;

import com.imposter.game.dto.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for all controllers.
 * Converts exceptions to consistent error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle all custom application exceptions.
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        log.warn("Application exception: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Handle validation errors from @Valid annotations.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Validation failed: {}", fieldErrors);

        ErrorResponse error = ErrorResponse.withFieldErrors(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Request validation failed",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle type mismatch errors (e.g., wrong parameter type).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = String.format(
                "Parameter '%s' should be of type '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        log.warn("Type mismatch: {}", message);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PARAMETER",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handle all unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}