package com.example.backend.dto.calendarValidationDTOS;

public class TravelInfoDTO {
    private final long seconds;
    private final String text;

    public TravelInfoDTO(long seconds, String text) {
        this.seconds = seconds;
        this.text = text;
    }

    public long getSeconds() {
        return seconds;
    }

    public String getText() {
        return text;
    }
}
