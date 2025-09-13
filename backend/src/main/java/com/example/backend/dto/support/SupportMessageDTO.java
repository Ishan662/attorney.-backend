package com.example.backend.dto.support;

import java.time.Instant;
import java.util.UUID;

public class SupportMessageDTO {
    private UUID id;
    private String messageBody;
    private Instant createdAt;
    private UUID sentByUserId;
    private String sentByUserName;
    private String sentByUserRole;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getMessageBody() { return messageBody; }
    public void setMessageBody(String body) { this.messageBody = body; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public UUID getSentByUserId() { return sentByUserId; }
    public void setSentByUserId(UUID id) { this.sentByUserId = id; }
    public String getSentByUserName() { return sentByUserName; }
    public void setSentByUserName(String name) { this.sentByUserName = name; }
    public String getSentByUserRole() { return sentByUserRole; }
    public void setSentByUserRole(String role) { this.sentByUserRole = role; }
}