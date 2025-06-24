package com.example.backend.model;

import jakarta.persistence.*;

@Entity
public class AppUser {

    @Id  // <-- This is required!
    private String firebaseUid;

    private String email;

    private boolean verified;

    private String role;

    // Getters and Setters

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}