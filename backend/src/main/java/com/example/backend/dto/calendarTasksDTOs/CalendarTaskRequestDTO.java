package com.example.backend.dto.calendarTasksDTOs;

import com.example.backend.model.CalendarTasks.TaskPriority;
import com.example.backend.model.CalendarTasks.TaskStatus;
import java.time.LocalDateTime;

public class CalendarTaskRequestDTO {
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TaskStatus status;
    private TaskPriority priority;

    public CalendarTaskRequestDTO() {}

    public CalendarTaskRequestDTO(String title, String description, String location,
                                  LocalDateTime startTime, LocalDateTime endTime,
                                  TaskStatus status, TaskPriority priority) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.priority = priority;
    }

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
}
