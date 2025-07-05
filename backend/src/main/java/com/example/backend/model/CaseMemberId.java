package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

// This class doesn't create a table. It just defines the structure of the composite key.
@Embeddable
public class CaseMemberId implements Serializable {

    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "user_id")
    private UUID userId;

    // A no-arg constructor is required by JPA
    public CaseMemberId() {}

    public CaseMemberId(UUID caseId, UUID userId) {
        this.caseId = caseId;
        this.userId = userId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // IMPORTANT: equals() and hashCode() must be implemented for composite keys to work correctly.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseMemberId that = (CaseMemberId) o;
        return Objects.equals(caseId, that.caseId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseId, userId);
    }
}