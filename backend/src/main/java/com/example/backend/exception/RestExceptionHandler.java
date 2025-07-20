// >> In a new file: exception/RestExceptionHandler.java
package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

// @ControllerAdvice tells Spring that this class will handle exceptions across the whole application.
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // --- ▼▼▼ THIS IS THE HANDLER FOR OUR SPECIFIC PROBLEM ▼▼▼ ---
    /**
     * Handles IllegalStateException.
     * We use this specifically for cases like "email already exists," which is a user input conflict.
     * It returns an HTTP 409 Conflict status.
     */
    @ExceptionHandler(value = { IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(IllegalStateException ex, WebRequest request) {
        // Create a simple JSON response body.
        Map<String, String> bodyOfResponse = Map.of("message", ex.getMessage());

        // Return a ResponseEntity with the body and the 409 status code.
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.CONFLICT);
    }
    // --- ▲▲▲ THIS IS THE HANDLER FOR OUR SPECIFIC PROBLEM ▲▲▲ ---


    // --- This is a good general-purpose handler to have for other errors ---
    /**
     * Handles IllegalArgumentException.
     * Use this for bad user input that isn't a conflict, e.g., "Role cannot be ADMIN."
     * It returns an HTTP 400 Bad Request status.
     */
    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        Map<String, String> bodyOfResponse = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }
}