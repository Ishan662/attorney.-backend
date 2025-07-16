// >> In a new file: dto/caseDTOS/CaseSummaryDTO.java
package com.example.backend.dto.caseDTOS;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CaseSummaryDTO {
    private UUID id;
    private String caseTitle;
    private String caseNumber;
    private String caseType;
    private String owner; // Full name of lawyer
    private String junior; // Full name of assigned junior
    private Instant nextHearing;
    private BigDecimal agreedFee;
    private String paymentStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getJunior() {
        return junior;
    }

    public void setJunior(String junior) {
        this.junior = junior;
    }

    public Instant getNextHearing() {
        return nextHearing;
    }

    public void setNextHearing(Instant nextHearing) {
        this.nextHearing = nextHearing;
    }

    public BigDecimal getAgreedFee() {
        return agreedFee;
    }

    public void setAgreedFee(BigDecimal agreedFee) {
        this.agreedFee = agreedFee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}