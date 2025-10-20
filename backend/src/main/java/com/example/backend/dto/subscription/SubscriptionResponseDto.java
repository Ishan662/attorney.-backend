// Create in a new file, e.g., dto/subscription/SubscriptionResponseDto.java
package com.example.backend.dto.subscription;

import com.example.backend.model.subcription.SubscriptionStatus;
import java.time.Instant;
import java.util.UUID;

public class SubscriptionResponseDto {

    private UUID id;
    private String planName;
    private SubscriptionStatus status;
    private Instant endDate;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
}