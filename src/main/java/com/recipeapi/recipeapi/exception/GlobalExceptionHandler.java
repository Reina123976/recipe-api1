package com.recipeapi.recipeapi.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Recipe API.
 *
 * <p>This class provides centralized exception handling across the API,
 * translating exceptions into appropriate HTTP responses.</p>
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles NotFoundException.
     *
     * <p>This method creates a 404 (Not Found) response when a NotFoundException is thrown.</p>
     *
     * @param ex The NotFoundException that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing error details
     */

    // Handle specific exceptions
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle validation exceptions
    /**
     * Handles validation exceptions.
     *
     * <p>This method creates a 400 (Bad Request) response when validation fails.</p>
     *
     * @param ex The MethodArgumentNotValidException that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Validation failed for one or more fields",
                request.getDescription(false)
        );

        return new ResponseEntity<>(Map.of("errors", errors, "details", errorResponse), HttpStatus.BAD_REQUEST);
    }

    // Handle global exceptions
    /**
     * Handles all other exceptions.
     *
     * <p>This method creates a 500 (Internal Server Error) response
     * for exceptions that aren't handled by more specific handlers.</p>
     *
     * @param ex The exception that was thrown
     * @param request The web request during which the exception was thrown
     * @return A ResponseEntity containing error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}