package com.example.backend.model.firm;

//  ▼▼▼ CHANGE THIS BLOCK ▼▼▼
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//  ▲▲▲ CHANGE THIS BLOCK ▲▲▲

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "firms")
public class Firm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String firmName;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirmName() { return firmName; }
    public void setFirmName(String firmName) { this.firmName = firmName; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}