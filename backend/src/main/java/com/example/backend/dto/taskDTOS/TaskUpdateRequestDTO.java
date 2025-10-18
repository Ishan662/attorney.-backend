package com.example.backend.dto.taskDTOS;

import com.example.backend.model.tasks.TaskStatus;

public class TaskUpdateRequestDTO {
    private TaskStatus status;

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}