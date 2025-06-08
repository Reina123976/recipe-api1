package com.recipeapi.recipeapi.exception;
import java.util.Date;

/**
 * Model class for standardized error responses.
 *
 * <p>This class provides a consistent structure for error responses
 * returned by the API when exceptions occur.</p>
 *
 */

public class ErrorResponse {

    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Default constructor that initializes the timestamp to the current date and time.
     */

    public ErrorResponse() {
        this.timestamp = new Date();
    }

    /**
     * Creates a new ErrorResponse with the specified details.
     *
     * @param status The HTTP status code
     * @param error The error type
     * @param message The error message
     * @param path The request path that generated the error
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters and Setters
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}