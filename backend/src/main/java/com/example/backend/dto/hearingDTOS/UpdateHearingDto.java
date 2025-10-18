package com.example.backend.dto.hearingDTOS;

import java.time.LocalDateTime;
import java.time.Instant;
import com.example.backend.model.hearing.HearingStatus;

public class UpdateHearingDto {
    private String title;
    private Instant hearingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String participants;
    private String googleMeetLink;
    private String note;
    private HearingStatus status;

    // --- Getters and Setters ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Instant getHearingDate() { return hearingDate; }
    public void setHearingDate(Instant hearingDate) { this.hearingDate = hearingDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getParticipants() { return participants; }
    public void setParticipants(String participants) { this.participants = participants; }

    public String getGoogleMeetLink() { return googleMeetLink; }
    public void setGoogleMeetLink(String googleMeetLink) { this.googleMeetLink = googleMeetLink; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public HearingStatus getStatus() { return status; }
    public void setStatus(HearingStatus status) { this.status = status; }
}
