package com.recipeapi.recipeapi.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exception thrown when a requested resource cannot be found.
 *
 * <p>This exception is used to indicate that a resource (e.g., a recipe or user)
 * requested by ID does not exist in the database.</p>
 *
 * <p>The exception is mapped to a 404 (Not Found) HTTP response.</p>
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /**
     * Creates a new NotFoundException with the specified error message.
     *
     * @param message The error message
     */

    public NotFoundException(String message) {
        super(message);
    }
}