package com.example.backend.payment.service;

import com.example.backend.payment.config.PayHereConfig;
import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayHereConfig payHereConfig;

    /**
     * Initiates a payment and generates PayHere hash for security.
     * This method creates a new Payment entity, saves it, and prepares
     * the data needed for frontend redirection to PayHere.
     */
    public PaymentResponseDto initiatePayment(PaymentRequestDto dto) {
        // Generate a unique order ID for this transaction
        String orderId = UUID.randomUUID().toString();

        // Create a new Payment entity and populate fields using setters
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setPayerEmail(dto.getPayerEmail());
        payment.setStatus("PENDING");
        payment.setReferenceType(dto.getReferenceType());
        payment.setReferenceId(dto.getReferenceId());

        // Save payment in DB before redirecting to PayHere
        paymentRepository.save(payment);

        // 🔒 Generate PayHere hash for security
        // Formula: base64( MD5(merchant_id + order_id + amount + currency + merchant_secret) )
        String hash = generatePayHereHash(
                payHereConfig.getMerchantId(),
                orderId,
                dto.getAmount(),
                dto.getCurrency(),
                payHereConfig.getMerchantSecret()
        );

        // Prepare response for frontend (which will redirect to PayHere)
        PaymentResponseDto response = new PaymentResponseDto();
        response.setOrderId(orderId);
        response.setHash(hash);
        response.setMerchantId(payHereConfig.getMerchantId());
        response.setAmount(dto.getAmount());
        response.setCurrency(dto.getCurrency());
        response.setReturnUrl(payHereConfig.getReturnUrl());
        response.setCancelUrl(payHereConfig.getCancelUrl());
        response.setNotifyUrl(payHereConfig.getNotifyUrl());

        return response;
    }

    /**
     * Generates a PayHere hash based on their documentation.
     */
    private String generatePayHereHash(String merchantId, String orderId, double amount, String currency, String merchantSecret) {
        try {
            // Format amount to 2 decimal places (PayHere requires this)
            String formattedAmount = String.format("%.2f", amount);

            // Combine data as per PayHere spec
            String rawData = merchantId + orderId + formattedAmount + currency + md5(merchantSecret);
            return md5(rawData).toUpperCase(); // PayHere uses uppercase MD5 hash
        } catch (Exception e) {
            throw new RuntimeException("Error generating PayHere hash", e);
        }
    }

    /**
     * Helper method for MD5 hashing (used in PayHere security).
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating MD5 hash", e);
        }
    }
}
