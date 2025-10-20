package com.example.backend.dto.meeting;

import com.example.backend.model.requests.RequestStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class MeetingResponseDTO {
    private UUID id;
    private String title;
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private RequestStatus status;
    private String note;
    private Instant createdAt;
    private UserInfoDTO client;
    private UserInfoDTO lawyer;
    private CaseInfoDTO aCase;

    public static class UserInfoDTO {
        private UUID id;
        private String firstName;
        private String lastName;
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class CaseInfoDTO {
        private UUID id;
        private String caseTitle;
        private String caseNumber;
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getCaseTitle() { return caseTitle; }
        public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
        public String getCaseNumber() { return caseNumber; }
        public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    }

    // Getters & Setters for all top-level fields...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public UserInfoDTO getClient() { return client; }
    public void setClient(UserInfoDTO client) { this.client = client; }
    public UserInfoDTO getLawyer() { return lawyer; }
    public void setLawyer(UserInfoDTO lawyer) { this.lawyer = lawyer; }
    public CaseInfoDTO getaCase() { return aCase; }
    public void setaCase(CaseInfoDTO aCase) { this.aCase = aCase; }
}