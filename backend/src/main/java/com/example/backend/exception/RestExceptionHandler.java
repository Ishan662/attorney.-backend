// >> In a new file: exception/RestExceptionHandler.java
package com.example.backend.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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
    @ExceptionHandler(value = {IllegalStateException.class})
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
    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex, WebRequest request) {
        Map<String, String> bodyOfResponse = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { DataIntegrityViolationException.class })
    protected ResponseEntity<Object> handleDatabaseConflict(DataIntegrityViolationException ex, WebRequest request) {
        String message = "A database error occurred. The requested action could not be completed."; // A safe default message

        // Get the most specific error message from the deepest cause in the exception chain.
        Throwable rootCause = ex.getRootCause();
        String causeMessage = (rootCause != null) ? rootCause.getMessage() : ex.getMessage();

        // Now, we perform simple and reliable string checks on the database error message.
        if (causeMessage != null) {
            String lowerCaseMessage = causeMessage.toLowerCase();

            // Check for keywords related to a unique key violation.
            if (lowerCaseMessage.contains("violates unique constraint")) {

                // Check for the name of our case number constraint.
                // You named this 'uk_firm_casenumber' in your Case entity.
                if (lowerCaseMessage.contains("uk_firm_casenumber")) {
                    message = "A case with this number already exists in your firm. Please use a unique case number.";
                }
                // Check for the name of the email constraint (PostgreSQL often names it table_column_key).
                else if (lowerCaseMessage.contains("users_email_key")) {
                    message = "This email address is already in use.";
                }
                // A good generic fallback if we add other unique constraints later.
                else {
                    message = "This value already exists and must be unique.";
                }
            }
            // You can add more checks for other types of DB errors here.
            // For example, foreign key violations:
            // else if (lowerCaseMessage.contains("violates foreign key constraint")) {
            //     message = "Cannot perform this action because it is related to other data.";
            // }
        }

        Map<String, String> bodyOfResponse = Map.of("message", message);
        return new ResponseEntity<>(bodyOfResponse, HttpStatus.CONFLICT);
    }

}