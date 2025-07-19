// >> In a new file: dto/hearingDTOS/CreateHearingDto.java
package com.example.backend.dto.hearingDTOS;

import java.time.Instant;

public class CreateHearingDto {
    private String title;
    private Instant hearingDate;
    private String location;
    private String note;
    // Status is not needed here as it will default to PLANNED on the backend.

    // --- Getters and Setters ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Instant getHearingDate() { return hearingDate; }
    public void setHearingDate(Instant hearingDate) { this.hearingDate = hearingDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}