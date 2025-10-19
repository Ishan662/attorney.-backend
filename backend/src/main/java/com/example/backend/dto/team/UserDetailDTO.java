package com.example.backend.dto.team;

import com.example.backend.model.AppRole;
import com.example.backend.model.UserStatus;
import com.example.backend.model.cases.CaseStatus; // Make sure this enum exists
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class UserDetailDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private AppRole role;
    private UserStatus status;
    private Instant createdAt;
    private List<CaseInfoDTO> assignedCases;

    // --- Inner DTO for Case Information ---
    public static class CaseInfoDTO {
        private UUID caseId;
        private String caseTitle;
        private String caseNumber;
        private CaseStatus status;

        // Getters & Setters for CaseInfoDTO
        public UUID getCaseId() { return caseId; }
        public void setCaseId(UUID caseId) { this.caseId = caseId; }
        public String getCaseTitle() { return caseTitle; }
        public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
        public String getCaseNumber() { return caseNumber; }
        public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
        public CaseStatus getStatus() { return status; }
        public void setStatus(CaseStatus status) { this.status = status; }
    }

    // --- Getters & Setters for UserDetailDTO ---
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
    public AppRole getRole() { return role; }
    public void setRole(AppRole role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<CaseInfoDTO> getAssignedCases() { return assignedCases; }
    public void setAssignedCases(List<CaseInfoDTO> assignedCases) { this.assignedCases = assignedCases; }
}