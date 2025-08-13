package com.example.backend.dto.lawyerDTOS;

import java.util.Map;
import java.util.UUID;

public class LawyerProfileDTO {
    private UUID userId;

    // This is the primary data for this DTO
    private Map<String, String> courtColors;

    // --- Getters and Setters ---
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public Map<String, String> getCourtColors() { return courtColors; }
    public void setCourtColors(Map<String, String> courtColors) { this.courtColors = courtColors; }
}
