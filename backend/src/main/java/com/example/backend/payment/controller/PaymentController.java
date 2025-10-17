package com.example.backend.payment.controller;

import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public PaymentResponseDto initiatePayment(@RequestBody PaymentRequestDto dto) {
        return paymentService.initiatePayment(dto);
    }

    /**
     * Webhook endpoint for PayHere to call after payment
     */
    @PostMapping("/payhere/webhook")
    public String handleWebhook(
            @RequestParam("merchant_id") String merchantId,
            @RequestParam("order_id") String orderId,
            @RequestParam("payhere_amount") double amount,
            @RequestParam("payhere_currency") String currency,
            @RequestParam("status_code") int statusCode,
            @RequestParam("md5sig") String md5sig
    ) {
        boolean verified = paymentService.handlePaymentNotification(
                merchantId, orderId, amount, currency, statusCode, md5sig
        );
        return verified ? "OK" : "FAIL";
    }
}
