// >> In a new file: CaseMember.java
package com.example.backend.model.cases;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "case_members")
public class CaseMember {

    // This tells JPA to use the CaseMemberId class as the primary key.
    @EmbeddedId
    private CaseMemberId id;

    // This defines the "many-to-one" part of the relationship back to the Case entity.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId") // This tells JPA that the 'caseId' field of our EmbeddedId maps to this relationship.
    @JoinColumn(name = "case_id")
    private Case aCase; // Using 'aCase' because 'case' is a reserved SQL keyword

    // This defines the "many-to-one" part of the relationship back to the User entity.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // This maps the 'userId' field of our EmbeddedId.
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Instant assignedAt = Instant.now();

    // A no-arg constructor is required by JPA
    public CaseMember() {}

    // A convenient constructor to make creating new memberships easy
    public CaseMember(Case aCase, User user) {
        this.aCase = aCase;
        this.user = user;
        this.id = new CaseMemberId(aCase.getId(), user.getId());
    }

    public CaseMemberId getId() {
        return id;
    }

    public void setId(CaseMemberId id) {
        this.id = id;
    }

    public Case getaCase() {
        return aCase;
    }

    public void setaCase(Case aCase) {
        this.aCase = aCase;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}