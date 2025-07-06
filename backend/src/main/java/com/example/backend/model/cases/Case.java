package com.example.backend.model.cases;

import com.example.backend.model.firm.Firm;
import jakarta.persistence.*;

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

    @Column
    private String caseNumber;

    @Column
    private String partyName;

    @Column
    private String caseType;

    @Column
    private String courtName;

    @Column(nullable = false)
    private boolean isClosed = false;

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

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
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
