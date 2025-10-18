package com.example.backend.model.hearing;

import com.example.backend.model.cases.Case;
import com.example.backend.model.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hearings")
public class Hearing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Link to Case entity
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "case_id", nullable = true)
    private Case aCase;

    // User who created this hearing
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "created_by_user_id", nullable = true)
    private User createdByUser;

    // Lawyer assigned to this hearing
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "lawyer_id", nullable = true)
    private User lawyer;

    @Column(nullable = true)
    private String title;

    /**
     * The general hearing date (e.g., "2025-09-08").
     * Useful for filtering by day.
     */
    @Column(nullable = true)
    private Instant hearingDate;

    /**
     * Start and end time for this hearing on that date.
     */
    @Column(nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingStatus status = HearingStatus.PLANNED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Case getaCase() { return aCase; }
    public void setaCase(Case aCase) { this.aCase = aCase; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    public User getLawyer() { return lawyer; }
    public void setLawyer(User lawyer) { this.lawyer = lawyer; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Instant getHearingDate() { return hearingDate; }
    public void setHearingDate(Instant hearingDate) { this.hearingDate = hearingDate; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public HearingStatus getStatus() { return status; }
    public void setStatus(HearingStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
