package com.example.backend.dto.InviteDTOS;

import com.example.backend.model.AppRole;

import java.util.UUID;

public class InviteUserRequest {
    private String email;
    private String fullName;
    private AppRole role;
    // For inviting clients to a specific case
    private java.util.UUID caseId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }
}
