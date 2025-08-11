package com.example.backend.dto.lawyerDTOS;

import java.util.Map;

public class CourtColorsRequest {
    private Long userId;
    private Map<String, String> courtColors;

    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public Map<String, String> getCourtColors() {
        return courtColors;
    }
    public void setCourtColors(Map<String, String> courtColors) {this.courtColors = courtColors;}
}
