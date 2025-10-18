package com.example.backend.dto.support;

import com.example.backend.model.support.SupportCaseStatus;
import java.time.Instant;
import java.util.UUID;

public class SupportCaseListDTO {
    private UUID id;
    private String publicId; // The human-readable ID like "T24-001"
    private String subject;
    private SupportCaseStatus status;
    private Instant createdAt;
    private String createdByUserName; // To show who created it in the admin view

    // Getters & Setters...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public SupportCaseStatus getStatus() { return status; }
    public void setStatus(SupportCaseStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getCreatedByUserName() { return createdByUserName; }
    public void setCreatedByUserName(String name) { this.createdByUserName = name; }
}