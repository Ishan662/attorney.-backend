package com.example.backend.payment.mapper;

import com.example.backend.payment.dto.PaymentDetailDto;
import com.example.backend.payment.model.Payment;
import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

    public PaymentDetailDto toPaymentDetailDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentDetailDto dto = new PaymentDetailDto();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getCreatedAt());

        // Safely access related Case entity data
        if (payment.getCaseEntity() != null) {
            dto.setClientName(payment.getCaseEntity().getClientName());
            dto.setCaseNumber(payment.getCaseEntity().getCaseNumber());
            dto.setCourt(payment.getCaseEntity().getCourtName());
        }

        return dto;
    }
}