package com.example.backend.dto.paymentsDTOs;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDTO {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String caseNumber;
    private String clientName;

    public PaymentDTO() {}

    // Getters and Setters
    public UUID getId() {
        return null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setPaymentDate(LocalDateTime createdAt) {
    }

    public void setCaseId(UUID id) {
    }

    public void setStatus(String name) {
    }
}
