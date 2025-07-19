package com.example.backend.dto.caseDTOS;

import com.example.backend.model.cases.CaseStatus;
import com.example.backend.model.cases.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for sending Case information to the client.
 * This represents the "public" view of a Case entity.
 */
public class CaseResponseDTO {

    // --- Core Case Information ---
    private UUID id;
    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String description;
    private String courtName;

    // --- Parties ---
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String opposingPartyName;

    // --- Status Fields ---
    private CaseStatus status;
    private PaymentStatus paymentStatus;

    // --- Financial Information ---
    private BigDecimal agreedFee;

    // --- Auditing/Metadata ---
    private Instant createdAt;
    private Instant updatedAt;

    // This will hold the full name of the creating lawyer.
    private String owner;

    // This will hold the date of the next upcoming hearing.
    private Instant nextHearing;

    // This will hold the full name of an assigned junior.
    private String junior;

    private String firmName;

    private String ownerLawyerName;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Instant getNextHearing() {
        return nextHearing;
    }

    public void setNextHearing(Instant nextHearing) {
        this.nextHearing = nextHearing;
    }

    public String getJunior() {
        return junior;
    }

    public void setJunior(String junior) {
        this.junior = junior;
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

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
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

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getOpposingPartyName() {
        return opposingPartyName;
    }

    public void setOpposingPartyName(String opposingPartyName) {
        this.opposingPartyName = opposingPartyName;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAgreedFee() {
        return agreedFee;
    }

    public void setAgreedFee(BigDecimal agreedFee) {
        this.agreedFee = agreedFee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}