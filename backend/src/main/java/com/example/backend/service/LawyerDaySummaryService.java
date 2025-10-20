package com.example.backend.service;

import com.example.backend.dto.caseDTOS.ClosedCasesDTO;
import com.example.backend.dto.paymentsDTOs.PaymentDTO;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.repository.PaymentRepository;
import com.example.backend.repositories.CaseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LawyerDaySummaryService {

    private final CaseRepository caseRepository;
    private final PaymentRepository paymentRepository;

    public LawyerDaySummaryService(CaseRepository caseRepository, PaymentRepository paymentRepository) {
        this.caseRepository = caseRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<ClosedCasesDTO> getClosedCasesForToday(UUID lawyerId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Case> closedCases = caseRepository.findClosedCasesForLawyerOnDate(
                lawyerId,
                CaseStatus.CLOSED,
                startOfDay,
                endOfDay
        );

        return closedCases.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ClosedCasesDTO> getOpenCasesForToday(UUID lawyerId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Case> openCases = caseRepository.findOpenedCasesForLawyerOnDate(
                lawyerId,
                CaseStatus.OPEN,
                startOfDay,
                endOfDay
        );

        return openCases.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> getTodaysPaymentsForFirm(UUID firmId) {
        // Use LocalDateTime for repository query
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDateTime startOfDay = today.atStartOfDay();               // LocalDateTime
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // Fetch payment entities for today
        List<Payment> payments = paymentRepository.findPaymentsByFirmForDay(firmId, startOfDay, endOfDay);

        // Map Payment entities to DTOs
        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Mapping method
    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setCaseId(payment.getCaseEntity().getId());

        // Convert Long to BigDecimal
        dto.setAmount(payment.getAmount() != null ? BigDecimal.valueOf(payment.getAmount()) : BigDecimal.ZERO);

        dto.setPaymentDate(payment.getCreatedAt()); // assuming createdAt is LocalDateTime
        dto.setStatus(payment.getStatus().name());

        // Add other fields if needed
        return dto;
    }




    // Mapping method
    private ClosedCasesDTO toDTO(Case c) {
        ClosedCasesDTO dto = new ClosedCasesDTO();
        dto.setId(c.getId());
        dto.setCaseTitle(c.getCaseTitle());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setClientName(c.getClientName());
        dto.setAgreedFee(c.getAgreedFee());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }



}
