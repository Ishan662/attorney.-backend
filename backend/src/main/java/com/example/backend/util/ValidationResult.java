package com.example.backend.util;

public class ValidationResult {
    private final boolean valid;
    private final String message;
    private final Long travelSeconds; // nullable
    private final String travelText;  // nullable

    // Main constructor
    private ValidationResult(boolean valid, String message, Long travelSeconds, String travelText) {
        this.valid = valid;
        this.message = message;
        this.travelSeconds = travelSeconds;
        this.travelText = travelText;
    }

    /**
     * Success result with custom message (no travel info)
     */
    public static ValidationResult ok(String message) {
        return new ValidationResult(true, message, null, null);
    }

    /**
     * Success result with default message
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, "Validation passed", null, null);
    }

    /**
     * Success result with travel info
     */
    public static ValidationResult ok(Long travelSeconds, String travelText) {
        return new ValidationResult(true, "Validation passed", travelSeconds, travelText);
    }

    /**
     * Failure result with message
     */
    public static ValidationResult fail(String message) {
        return new ValidationResult(false, message, null, null);
    }

    /**
     * Getters
     */
    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Long getTravelSeconds() {
        return travelSeconds;
    }

    public String getTravelText() {
        return travelText;
    }

    /**
     * Optional: convenience method for frontend-friendly JSON
     */
    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                ", travelSeconds=" + travelSeconds +
                ", travelText='" + travelText + '\'' +
                '}';
    }
}
