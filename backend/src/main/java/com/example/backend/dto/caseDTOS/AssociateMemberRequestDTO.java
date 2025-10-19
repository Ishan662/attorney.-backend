package com.example.backend.dto.caseDTOS;

import java.util.UUID;

public class AssociateMemberRequestDTO {
    private UUID userId;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}