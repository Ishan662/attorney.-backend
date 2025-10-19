package com.example.backend.payment.controller;

import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody PaymentRequestDto request) {
        try {
            PaymentResponseDto response = paymentService.createCheckoutSession(request);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Stripe API error during payment initiation: {}", e.getMessage());
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during payment initiation for user {}", request.getCustomerEmail(), e);
            return ResponseEntity
                    .internalServerError()
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    @GetMapping("/total-paid/{caseId}")
    public ResponseEntity<?> getTotalPaidAmount(@PathVariable UUID caseId) {
        try {
            Long totalPaid = paymentService.getTotalPaidForCase(caseId);
            // We return the amount in a simple JSON object: { "totalPaidAmount": 5000 }
            return ResponseEntity.ok(Map.of("totalPaidAmount", totalPaid));
        } catch (Exception e) {
            // Basic error handling
            return ResponseEntity.internalServerError().body("Error retrieving total paid amount.");
        }
    }
}