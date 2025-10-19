package com.example.backend.dto.team;

import java.math.BigDecimal;

public class UpdateSalaryRequestDTO {
    private BigDecimal newMonthlySalary;
    public BigDecimal getNewMonthlySalary() { return newMonthlySalary; }
    public void setNewMonthlySalary(BigDecimal newMonthlySalary) { this.newMonthlySalary = newMonthlySalary; }
}
