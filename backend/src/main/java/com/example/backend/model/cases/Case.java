package com.example.backend.model.cases;

import com.example.backend.model.firm.Firm;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Every case must belong to a firm. If the firm is deleted, its cases are also deleted.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "firm_id", nullable = false)
    private Firm firm;

    @Column(nullable = false)
    private String caseTitle;

    @Column(unique = true) // Case numbers should be unique within the system or at least a firm
    private String caseNumber;

    @Column
    private String partyName;

    @Column
    private String caseType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String courtName;

    // Replaces the 'isClosed' boolean with a more descriptive status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.OPEN;

    // Financial Fields from your UI
    @Column(precision = 19, scale = 4) // Good practice for money
    private BigDecimal agreedFee;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_INVOICED;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalExpenses;

    @Column(precision = 19, scale = 4)
    private BigDecimal invoicedAmount;


    // This defines the "other side" of the relationship. It tells JPA that this Case
    // can have a set of members, and the relationship details are managed in the 'CaseMember' entity.
    @OneToMany(
            mappedBy = "aCase", // 'aCase' is the field name in the CaseMember class
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<CaseMember> members = new HashSet<>();


    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
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

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getInvoicedAmount() {
        return invoicedAmount;
    }

    public void setInvoicedAmount(BigDecimal invoicedAmount) {
        this.invoicedAmount = invoicedAmount;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
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

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }


    public Set<CaseMember> getMembers() {
        return members;
    }

    public void setMembers(Set<CaseMember> members) {
        this.members = members;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
