package com.example.backend.model.hearing;

import com.example.backend.model.cases.Case;
import com.example.backend.model.user.User;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "hearings")
public class Hearing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // This relationship links back to the Case entity in the 'cases' package.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case aCase;

    // This links to the User entity in the 'user' package.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private User lawyer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Instant hearingDate;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String note;

    // This uses the HearingStatus enum from within its own package.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingStatus status = HearingStatus.PLANNED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


    // --- Getters and Setters ---

    public User getLawyer() {
        return lawyer;
    }

    public void setLawyer(User lawyer) {
        this.lawyer = lawyer;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Case getaCase() { return aCase; }
    public void setaCase(Case aCase) { this.aCase = aCase; }
    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Instant getHearingDate() { return hearingDate; }
    public void setHearingDate(Instant hearingDate) { this.hearingDate = hearingDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public HearingStatus getStatus() { return status; }
    public void setStatus(HearingStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}