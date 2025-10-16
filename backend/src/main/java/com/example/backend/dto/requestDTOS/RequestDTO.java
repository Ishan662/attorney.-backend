package com.example.backend.dto.requestDTOS;

import com.example.backend.model.requests.RequestStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class RequestDTO {
    private UUID id;
    private UUID caseId;
    private UUID createdByClientId;
    private String title;
    private String location;
    private String note;
    private LocalDateTime requestedDate;
    private Instant createdAt;
    private Instant updatedAt;
    private RequestStatus status;
    private UUID requestedLawyerId;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public UUID getCreatedByClientId() { return createdByClientId; }
    public void setCreatedByClientId(UUID createdByClientId) { this.createdByClientId = createdByClientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDateTime requestedDate) { this.requestedDate = requestedDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public UUID getRequestedLawyerId() { return requestedLawyerId; }
    public void setRequestedLawyerId(UUID requestedLawyerId) { this.requestedLawyerId = requestedLawyerId; }
}

