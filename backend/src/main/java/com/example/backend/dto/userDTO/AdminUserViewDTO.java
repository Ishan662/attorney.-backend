// >> In a new file: dto/userDTO/AdminUserViewDTO.java
package com.example.backend.dto.userDTO;

import com.example.backend.model.AppRole;
import com.example.backend.model.UserStatus;
import java.time.Instant;
import java.util.UUID;

// This DTO is specifically for the Admin User Management view.
public class AdminUserViewDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private AppRole role;
    private UserStatus status;
    private Instant dateJoined;

    // --- Lawyer-specific fields ---
    private String firmName;
    private Long clientCount;
    private Long juniorLawyerCount;

    // --- Junior-specific fields ---
    private String seniorLawyerName; // Name of a lawyer in their firm

    // --- Client-specific fields ---
    private String associatedLawyerName;
    private Long caseCount;

    // (Generate these with your IDE)

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Instant getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Instant dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public Long getClientCount() {
        return clientCount;
    }

    public void setClientCount(Long clientCount) {
        this.clientCount = clientCount;
    }

    public Long getJuniorLawyerCount() {
        return juniorLawyerCount;
    }

    public void setJuniorLawyerCount(Long juniorLawyerCount) {
        this.juniorLawyerCount = juniorLawyerCount;
    }

    public String getSeniorLawyerName() {
        return seniorLawyerName;
    }

    public void setSeniorLawyerName(String seniorLawyerName) {
        this.seniorLawyerName = seniorLawyerName;
    }

    public String getAssociatedLawyerName() {
        return associatedLawyerName;
    }

    public void setAssociatedLawyerName(String associatedLawyerName) {
        this.associatedLawyerName = associatedLawyerName;
    }

    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }
}
