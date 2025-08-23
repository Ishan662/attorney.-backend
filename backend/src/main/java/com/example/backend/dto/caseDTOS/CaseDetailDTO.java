// >> In a new file: dto/caseDTOS/CaseDetailDTO.java
package com.example.backend.dto.caseDTOS;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.model.cases.CaseStatus;
import com.example.backend.model.cases.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class CaseDetailDTO {
    // Core Details
    private UUID id;
    private String caseTitle;
    private String caseNumber;
    private String caseType;
    private String description;
    private String courtName;
    private String courtType;
    private CaseStatus status;

    // Parties
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String opposingPartyName;
    private String junior; // Name of an assigned junior

    // Financials
    private BigDecimal agreedFee;
    private PaymentStatus paymentStatus;
    // You can add totalExpenses, etc. later

    // Associated Lists
    private List<HearingDTO> hearings;
    // private List<DocumentDTO> documents; // For the future

    // details of the lawyer and firm
    private String ownerLawyerName; // To hold the name of the primary lawyer
    private String firmName;

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public String getOwnerLawyerName() {
        return ownerLawyerName;
    }

    public void setOwnerLawyerName(String ownerLawyerName) {
        this.ownerLawyerName = ownerLawyerName;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getOpposingPartyName() {
        return opposingPartyName;
    }

    public void setOpposingPartyName(String opposingPartyName) {
        this.opposingPartyName = opposingPartyName;
    }

    public String getJunior() {
        return junior;
    }

    public void setJunior(String junior) {
        this.junior = junior;
    }

    public BigDecimal getAgreedFee() {
        return agreedFee;
    }

    public void setAgreedFee(BigDecimal agreedFee) {
        this.agreedFee = agreedFee;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<HearingDTO> getHearings() {
        return hearings;
    }

    public void setHearings(List<HearingDTO> hearings) {
        this.hearings = hearings;
    }
}