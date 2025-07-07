package com.example.backend.dto.userDTO;

import com.example.backend.model.AppRole;
import java.util.UUID;

// This is a simple data-holding class. It has no JPA annotations.
public class UserDTO {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Integer phoneNumber;
    private String fullName; // We can pre-calculate this for the frontend's convenience
    private AppRole role;
    private UUID firmId;
    private String firmName;


    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public UUID getFirmId() {
        return firmId;
    }

    public void setFirmId(UUID firmId) {
        this.firmId = firmId;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }
}