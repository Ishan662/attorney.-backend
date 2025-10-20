package com.example.backend.payment.service;

import com.example.backend.model.cases.Case;
import com.example.backend.model.firm.Firm;
import com.example.backend.model.user.User;
import com.example.backend.payment.dto.OverdueCaseDto;
import com.example.backend.payment.dto.PaymentDetailDto;
import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.mapper.PaymentMapper;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.service.AuthService;
import com.example.backend.service.EmailService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CaseRepository caseRepository;
    private final AuthService authService;
    private final PaymentMapper paymentMapper;
    private final EmailService emailService;

    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    public PaymentService(PaymentRepository paymentRepository, CaseRepository caseRepository, AuthService authService, PaymentMapper paymentMapper, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.caseRepository = caseRepository;
        this.authService = authService;
        this.paymentMapper = paymentMapper;
        this.emailService = emailService;
    }

    @Transactional
    public PaymentResponseDto createCheckoutSession(PaymentRequestDto request) throws StripeException {
        UUID caseId = UUID.fromString(request.getCaseId());
        Case caseToLink = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with ID: " + request.getCaseId()));

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
                                                ).build()
                                ).build()
                ).build();

        Session session = Session.create(params);

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

    public Long getTotalPaidForCase(UUID caseId) {
        return paymentRepository.sumSuccessfulPaymentsByCaseId(caseId, PaymentStatus.SUCCESS);
    }

    public List<PaymentDetailDto> getPaymentsForCurrentLawyer() {
        User currentUser = authService.getCurrentUser();
        Firm firm = currentUser.getFirm();

        if (firm == null) {
            return Collections.emptyList();
        }

        List<Payment> payments = paymentRepository.findAllByFirmId(firm.getId());

        return payments.stream()
                .map(paymentMapper::toPaymentDetailDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a summary of cases with overdue payments for the current lawyer's firm.
     * A case is considered overdue if it was created more than 2 months ago and is not fully paid.
     * @return A list of overdue case details.
     */
    public List<OverdueCaseDto> getOverdueCasesForCurrentLawyer() {
        User currentUser = authService.getCurrentUser();
        Firm firm = currentUser.getFirm();
        if (firm == null) {
            return Collections.emptyList();
        }

        // Define the cutoff date as 2 months ago
        Instant twoMonthsAgo = Instant.now().minus(60, ChronoUnit.DAYS);

        // Call the new, efficient query on the CaseRepository
        return caseRepository.findOverdueCasesByFirm(firm.getId(), twoMonthsAgo);
    }

    public void sendOverduePaymentReminders() {
        User currentLawyer = authService.getCurrentUser();
        List<OverdueCaseDto> overdueCases = getOverdueCasesForCurrentLawyer();

        if (overdueCases.isEmpty()) {
            System.out.println("No overdue payments to send reminders for.");
            return;
        }

        for (OverdueCaseDto overdueCase : overdueCases) {
            String to = overdueCase.getClientEmail();
            String subject = "Friendly Reminder: Payment Due for Case " + overdueCase.getCaseNumber();

            BigDecimal agreedFee = overdueCase.getAgreedFee();
            BigDecimal totalPaid = new BigDecimal(overdueCase.getTotalPaidAmount()).divide(new BigDecimal(100));
            BigDecimal amountDue = agreedFee.subtract(totalPaid);

            String text = String.format(
                    "Dear %s,\n\n" +
                            "This is a friendly reminder regarding the outstanding payment for your case: '%s'.\n\n" +
                            "Agreed Fee: $%.2f\n" +
                            "Amount Paid: $%.2f\n" +
                            "Amount Due: $%.2f\n\n" +
                            "Please arrange for the payment at your earliest convenience.\n\n" +
                            "Sincerely,\n%s's Law Firm",
                    overdueCase.getClientName(),
                    overdueCase.getCaseTitle(),
                    agreedFee,
                    totalPaid,
                    amountDue,
                    currentLawyer.getFirm().getFirmName()
            );

            emailService.sendSimpleMessage(to, subject, text);
        }
    }
}