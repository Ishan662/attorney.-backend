package com.example.backend.dto.taskDTOS;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class TaskResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String status;
    private Date dueDate;
    private LocalDateTime createdAt;
    private UUID caseId;
    private UserInfoDTO assignedByUser;
    private UserInfoDTO assignedToUser;

    // --- Inner DTO for User Information ---
    public static class UserInfoDTO {
        private UUID id;
        private String firstName;
        private String lastName;

        public UserInfoDTO() {}

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    // --- Getters and Setters for TaskResponseDTO ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UserInfoDTO getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(UserInfoDTO assignedByUser) { this.assignedByUser = assignedByUser; }
    public UserInfoDTO getAssignedToUser() { return assignedToUser; }
    public void setAssignedToUser(UserInfoDTO assignedToUser) { this.assignedToUser = assignedToUser; }
}