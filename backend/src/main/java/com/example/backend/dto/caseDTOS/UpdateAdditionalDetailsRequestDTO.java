package com.example.backend.dto.caseDTOS;

import java.util.Map;

/**
 * A generic DTO to update the 'details' JSONB field of a Case.
 * The frontend is expected to send the complete, updated 'additionalDetails' object.
 */
public class UpdateAdditionalDetailsRequestDTO {

    private Map<String, Object> additionalDetails;

    // Getter and Setter
    public Map<String, Object> getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(Map<String, Object> additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
}