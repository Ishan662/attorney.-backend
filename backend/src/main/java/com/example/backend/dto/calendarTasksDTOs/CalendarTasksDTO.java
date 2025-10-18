package com.example.backend.dto.calendarTasksDTOs;

import com.example.backend.model.CalendarTasks.TaskPriority;
import com.example.backend.model.CalendarTasks.TaskStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class CalendarTasksDTO {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant createdAt;

    public CalendarTasksDTO() {}

    public CalendarTasksDTO(UUID id, String title, String description, String location,
                           LocalDateTime startTime, LocalDateTime endTime,
                           TaskStatus status, TaskPriority priority, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
