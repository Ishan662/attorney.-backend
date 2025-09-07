package com.example.backend.dto.userDTO;

import com.example.backend.model.UserStatus;

public class UpdateUserStatusRequest {
    private UserStatus newStatus;

    // Getter and Setter
    public UserStatus getNewStatus() { return newStatus; }
    public void setNewStatus(UserStatus newStatus) { this.newStatus = newStatus; }
}