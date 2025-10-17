package com.example.backend.payment.dto;

import lombok.Data;

/**
 * Response returned to frontend after payment initialization.
 */
@Data
public class PaymentResponseDto {
    private String paymentUrl;
    private String orderId;
}
