package com.example.backend.dto.team;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecordSalaryPaymentRequestDTO {
    private BigDecimal amountPaid;
    private LocalDate paymentDate;
    private String notes;

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}