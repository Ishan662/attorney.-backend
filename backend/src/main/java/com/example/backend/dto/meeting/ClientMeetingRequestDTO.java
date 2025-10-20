package com.example.backend.dto.meeting;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class ClientMeetingRequestDTO {
    @NotNull(message = "Case ID is required.")
    private UUID caseId;
    @NotNull(message = "Title is required.")
    private String title;
    @NotNull(message = "Meeting date is required.")
    @FutureOrPresent(message = "Meeting date must be in the present or future.")
    private LocalDate meetingDate;
    @NotNull(message = "Start time is required.")
    private LocalTime startTime;
    @NotNull(message = "End time is required.")
    private LocalTime endTime;
    private String note;

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}