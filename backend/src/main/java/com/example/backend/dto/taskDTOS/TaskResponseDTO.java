package com.example.backend.dto.taskDTOS;

import com.fasterxml.jackson.core.JsonToken;
import lombok.*;

import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {
    private UUID id;
    private UUID caseId;
    private String title;
    private String description;
    private UUID assignedByUser;
    private UUID assignedToUser;
    private String status;


    public UUID getAssignedByUser() {
        return assignedByUser;
    }

    public void setAssignedByUser(UUID assignedByUser) {
        this.assignedByUser = assignedByUser;
    }

    public UUID getAssignedToUser() {
        return assignedToUser;
    }

    public void setAssignedToUser(UUID assignedToUser) {
        this.assignedToUser = assignedToUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status; // ✅ fixed
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
