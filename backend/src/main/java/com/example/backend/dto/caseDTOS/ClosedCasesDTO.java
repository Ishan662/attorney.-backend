package com.example.backend.dto.caseDTOS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ClosedCasesDTO {
    private UUID id;
    private String caseTitle;
    private String caseNumber;
    private String clientName;
    private BigDecimal agreedFee;
    private String status; // CaseStatus as string
    private Instant createdAt;
    private Instant updatedAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public BigDecimal getAgreedFee() { return agreedFee; }
    public void setAgreedFee(BigDecimal agreedFee) { this.agreedFee = agreedFee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
