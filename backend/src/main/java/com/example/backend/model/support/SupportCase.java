package com.example.backend.model.support;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_cases")
public class SupportCase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupportCaseStatus status = SupportCaseStatus.OPEN;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant closedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPublicId() { return publicId; }
    public void setPublicId(String publicId) { this.publicId = publicId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
    public SupportCaseStatus getStatus() { return status; }
    public void setStatus(SupportCaseStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
}