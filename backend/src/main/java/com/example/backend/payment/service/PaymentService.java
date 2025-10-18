package com.example.backend.payment.service;

import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponseDto createCheckoutSession(PaymentRequestDto request) throws StripeException {
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
                                                .setUnitAmount(request.getAmount()) // Use the Long value directly
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

        Payment payment = new Payment(
                session.getId(),
                request.getCustomerEmail(),
                request.getAmount(),
                request.getCurrency(),
                PaymentStatus.CREATED, // Use Enum
                request.getDescription()
        );
        paymentRepository.save(payment);

        return new PaymentResponseDto(session.getId(), session.getUrl());
    }
}