package com.example.backend.model.tasks;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Builder

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String type;
    private String description;
    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "assigned_by_user_id")
    private User assignedByUser;

    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedToUser;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private UUID caseId;

    private boolean completed = false;

    @ElementCollection
    private List<String> documentUrls;

    // ------------------- Constructors -------------------
    public Task() {} // Hibernate requires no-arg constructor

    public Task(UUID id, String title, String type, String description, Date dueDate,
                User assignedByUser, User assignedToUser, TaskStatus status, TaskPriority priority,
                UUID caseId, boolean completed, List<String> documentUrls) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.dueDate = dueDate;
        this.assignedByUser = assignedByUser;
        this.assignedToUser = assignedToUser;
        this.status = status;
        this.priority = priority;
        this.caseId = caseId;
        this.completed = completed;
        this.documentUrls = documentUrls;
    }

    // ------------------- Getters & Setters -------------------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public User getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(User assignedByUser) { this.assignedByUser = assignedByUser; }

    public User getAssignedToUser() { return assignedToUser; }
    public void setAssignedToUser(User assignedToUser) { this.assignedToUser = assignedToUser; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public List<String> getDocumentUrls() { return documentUrls; }
    public void setDocumentUrls(List<String> documentUrls) { this.documentUrls = documentUrls; }
}
