package com.example.backend.dto.hearingDTOS;

import com.example.backend.model.hearing.HearingStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class HearingDTO {
    private UUID id;
    private String title;
    private Instant hearingDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String note;
    private HearingStatus status;

    // 👇 add these two lines
    private UUID caseId;
    private String caseTitle;

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public HearingStatus getStatus() { return status; }
    public void setStatus(HearingStatus status) { this.status = status; }

    // 👇 new getters/setters
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
}
