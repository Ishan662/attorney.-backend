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
    private String title;
    private String description;
    private UUID assignedByUserId;
    private UUID assignedToUserId;
    private UUID caseId;
    private String status;

    public static JsonToken builder() {
        return null;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public UUID getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(UUID assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public UUID getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(UUID assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
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
