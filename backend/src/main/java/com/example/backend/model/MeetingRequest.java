package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meeting_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title of the meeting (client provided)
    @Column(nullable = false)
    private String title;

    // Location or address
    private String location;

    // Date of meeting
    @Column(nullable = false)
    private LocalDate meetingDate;

    // Start and end time
    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // Comma separated guest emails or names (simple)
    @Column(length = 1000)
    private String guests;

    @Column(length = 2000)
    private String specialNote;

    // If a generated meet link is saved
    private String googleMeetLink;

    // Request status (PENDING / APPROVED / REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    // Audit
    private String createdBy; // could be client email or user id
    private String updatedBy;

    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = java.time.Instant.now();
        updatedAt = createdAt;
        if (status == null) status = MeetingStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = java.time.Instant.now();
    }
}

