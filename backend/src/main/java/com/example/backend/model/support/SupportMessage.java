package com.example.backend.model.support;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "support_messages")
public class SupportMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "support_case_id", nullable = false)
    private SupportCase supportCase;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sent_by_user_id", nullable = false)
    private User sentByUser;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageBody;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Getters & Setters...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SupportCase getSupportCase() { return supportCase; }
    public void setSupportCase(SupportCase supportCase) { this.supportCase = supportCase; }
    public User getSentByUser() { return sentByUser; }
    public void setSentByUser(User sentByUser) { this.sentByUser = sentByUser; }
    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String messageBody) { this.messageBody = messageBody; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}