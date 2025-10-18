package com.example.backend.dto.taskDTOS;

import java.util.List;
import java.util.UUID;

public class UpdateTaskRequestDTO {
    private UUID taskId;
    private List<String> documentUrls;
    private boolean completed;

    // getters and setters
    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public List<String> getDocumentUrls() { return documentUrls; }
    public void setDocumentUrls(List<String> documentUrls) { this.documentUrls = documentUrls; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
