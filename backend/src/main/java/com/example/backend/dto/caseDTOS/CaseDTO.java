package com.example.backend.dto.caseDTOS;

import com.example.backend.model.cases.CaseStatus;
import com.example.backend.model.cases.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// This is a plain Java object (POJO). It has no database annotations.
// Its only job is to carry data between the service layer and the controller.
public class CaseDTO {

    // --- Core Case Information ---
    private UUID id;
    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String description;
    private String partyName;
    private String courtName;
    private String courtType;

    // --- Status Fields ---
    private CaseStatus status;
    private PaymentStatus paymentStatus;

    // --- Financial Information ---
    private BigDecimal agreedFee;
    private BigDecimal totalExpenses;
    private BigDecimal invoicedAmount;

    // --- Auditing/Metadata ---
    private Instant createdAt;
    private Instant updatedAt;

    // --- Getters and Setters for ALL fields ---
    // (It's very important to have getters and setters for all these fields
    // so the mapping and JSON serialization libraries can work correctly.)


    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }
    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public BigDecimal getAgreedFee() { return agreedFee; }
    public void setAgreedFee(BigDecimal agreedFee) { this.agreedFee = agreedFee; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    public BigDecimal getInvoicedAmount() { return invoicedAmount; }
    public void setInvoicedAmount(BigDecimal invoicedAmount) { this.invoicedAmount = invoicedAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}