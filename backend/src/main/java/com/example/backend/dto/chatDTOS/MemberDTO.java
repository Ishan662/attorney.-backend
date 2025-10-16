package com.example.backend.dto.chatDTOS;

import com.example.backend.model.AppRole;
import java.util.UUID;

public class MemberDTO {
    private UUID userId;
    private String name;
    private AppRole role;

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AppRole getRole() { return role; }
    public void setRole(AppRole role) { this.role = role; }
}