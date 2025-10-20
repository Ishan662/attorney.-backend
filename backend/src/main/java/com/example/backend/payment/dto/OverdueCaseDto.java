package com.example.backend.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OverdueCaseDto {

    private UUID caseId;
    private String caseTitle;
    private String clientName;
    private String clientEmail;
    private String caseNumber;
    private String courtName;
    private Instant caseCreationDate;
    private BigDecimal agreedFee;
    private Long totalPaidAmount; // in cents

    // --- ▼▼▼ THIS CONSTRUCTOR IS NOW CORRECTED ▼▼▼ ---
    // It now accepts all 9 arguments to match the query
    public OverdueCaseDto(UUID caseId, String caseTitle, String clientName, String clientEmail, String caseNumber, String courtName, Instant caseCreationDate, BigDecimal agreedFee, Long totalPaidAmount) {
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.clientName = clientName;
        this.clientEmail = clientEmail; // Add assignment for clientEmail
        this.caseNumber = caseNumber;
        this.courtName = courtName;
        this.caseCreationDate = caseCreationDate;
        this.agreedFee = agreedFee;
        this.totalPaidAmount = totalPaidAmount;
    }

    // Getters
    public UUID getCaseId() { return caseId; }
    public String getCaseTitle() { return caseTitle; }
    public String getClientName() { return clientName; }
    public String getClientEmail() { return clientEmail; }
    public String getCaseNumber() { return caseNumber; }
    public String getCourtName() { return courtName; }
    public Instant getCaseCreationDate() { return caseCreationDate; }
    public BigDecimal getAgreedFee() { return agreedFee; }
    public Long getTotalPaidAmount() { return totalPaidAmount; }
}