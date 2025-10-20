package com.example.backend.dto.support;

import java.time.Instant;
import java.util.UUID;

public class SupportCaseSummaryDTO {
    private UUID id;
    private String subject;
    private String lawyerName;
    private String lawyerEmail;
    private Instant createdAt;
    private String status;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getLawyerName() { return lawyerName; }
    public void setLawyerName(String lawyerName) { this.lawyerName = lawyerName; }

    public String getLawyerEmail() { return lawyerEmail; }
    public void setLawyerEmail(String lawyerEmail) { this.lawyerEmail = lawyerEmail; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
