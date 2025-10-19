package com.example.backend.dto.team;

import com.example.backend.model.UserStatus;
import java.time.Instant;
import java.util.UUID;

public class ClientOverviewDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private Instant createdAt;
    private long assignedCasesCount;

    // Getters & Setters for all fields
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public long getAssignedCasesCount() { return assignedCasesCount; }
    public void setAssignedCasesCount(long assignedCasesCount) { this.assignedCasesCount = assignedCasesCount; }
}
