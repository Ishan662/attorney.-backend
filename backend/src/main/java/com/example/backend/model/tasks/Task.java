package com.example.backend.model.tasks;

import com.example.backend.model.tasks.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String description;

    private UUID assignedByUserId;
    private UUID assignedToUserId;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private UUID caseId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(UUID assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public UUID getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(UUID assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }
}
