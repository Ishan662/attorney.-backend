package com.example.backend.dto.hearingDTOS;

import java.time.LocalDateTime;
import java.time.Instant;

public class CreateHearingDto {
    private String title;
    private Instant hearingDate;      // main date for filtering
    private Instant startTime;  // start time of the hearing
    private Instant endTime;    // end time of the hearing
    private String location;
    private String participants;      // optional
    private String googleMeetLink;    // optional
    private String note;

    // --- Getters and Setters ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Instant getHearingDate() { return hearingDate; }
    public void setHearingDate(Instant hearingDate) { this.hearingDate = hearingDate; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getParticipants() { return participants; }
    public void setParticipants(String participants) { this.participants = participants; }

    public String getGoogleMeetLink() { return googleMeetLink; }
    public void setGoogleMeetLink(String googleMeetLink) { this.googleMeetLink = googleMeetLink; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
