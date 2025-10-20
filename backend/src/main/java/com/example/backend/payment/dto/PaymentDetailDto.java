package com.example.backend.payment.dto;

import com.example.backend.payment.model.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDetailDto {

    private UUID id;
    private String clientName;
    private String caseNumber;
    private String court;
    private LocalDateTime paymentDate;
    private Long amount; // in cents
    private PaymentStatus status;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getCourt() { return court; }
    public void setCourt(String court) { this.court = court; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}