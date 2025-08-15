package com.example.backend.model.requests;

import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.HearingStatus;
import com.example.backend.model.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "requests")

public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // This relationship links back to the Case entity in the 'cases' package.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case aCase;

    // This links to the User entity in the 'user' package.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_client_id", nullable = false)
    private User createdByClient;

    @Column(nullable = false)
    private String title;

    @Column
    private String location;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private java.time.LocalDateTime requestedDate;

    public void setRequestedDate(java.time.LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false, updatable = false)
    private Instant updatedAt = Instant.now();

    @ManyToOne
    @JoinColumn(name = "request_status_id")
    // This uses the HearingStatus enum from within its own package.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    // This links to the User entity in the 'user' package.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_lawyer_id", nullable = false)
    private User requestedByLawyer;

}


