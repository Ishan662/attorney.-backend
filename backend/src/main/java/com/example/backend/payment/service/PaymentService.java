package com.example.backend.payment.service;

import com.example.backend.model.cases.Case;
import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.example.backend.repositories.CaseRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CaseRepository caseRepository; // Inject CaseRepository

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    // Update constructor to accept CaseRepository
    public PaymentService(PaymentRepository paymentRepository, CaseRepository caseRepository) {
        this.paymentRepository = paymentRepository;
        this.caseRepository = caseRepository;
    }

    @Transactional
    public PaymentResponseDto createCheckoutSession(PaymentRequestDto request) throws StripeException {
        // 1. Find the Case entity from the database
        UUID caseId = UUID.fromString(request.getCaseId());
        Case caseToLink = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with ID: " + request.getCaseId()));

        // 2. Create the Stripe Session
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(request.getCurrency())
                                                .setUnitAmount(request.getAmount())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(request.getDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        // 3. Create the Payment entity with the LINKED Case object
        Payment payment = new Payment(
                caseToLink,
                session.getId(),
                request.getCustomerEmail(),
                request.getAmount(),
                request.getCurrency(),
                PaymentStatus.CREATED,
                request.getDescription()
        );
        paymentRepository.save(payment);

        return new PaymentResponseDto(session.getId(), session.getUrl());
    }

    /**
     * Retrieves the total amount paid for a specific case.
     * @param caseId The UUID of the case.
     * @return The total paid amount in the smallest currency unit (e.g., cents).
     */
    public Long getTotalPaidForCase(UUID caseId) {
        return paymentRepository.sumSuccessfulPaymentsByCaseId(caseId, PaymentStatus.SUCCESS);
    }
}