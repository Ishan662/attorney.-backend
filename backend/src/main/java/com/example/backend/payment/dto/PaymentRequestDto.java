package com.example.backend.payment.dto;

import lombok.Data;

/**
 * Incoming payment request from frontend or other module.
 */
@Data
public class PaymentRequestDto {
    private double amount;
    private String currency;
    private String payerEmail;
    private String referenceType; // optional - identifies module
    private String referenceId;   // optional - identifies record
}
