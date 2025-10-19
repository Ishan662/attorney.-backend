package com.example.backend.dto.team;

import com.example.backend.model.UserStatus;

public class UpdateUserStatusRequestDTO {
    private UserStatus newStatus;

    public UserStatus getNewStatus() { return newStatus; }
    public void setNewStatus(UserStatus newStatus) { this.newStatus = newStatus; }
}
