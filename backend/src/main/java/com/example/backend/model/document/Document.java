package com.example.backend.model.document;

import com.example.backend.model.tasks.Task;
import com.example.backend.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The name the user gave the file (e.g., "ClientContract.pdf")
    @Column(nullable = false)
    private String originalFileName;

    // The unique, random name we give the file on the server's disk
    @Column(nullable = false, unique = true)
    private String storedFileName;

    @Column(nullable = false)
    private long fileSize; // in bytes

    // The MIME type (e.g., "application/pdf", "image/jpeg")
    private String fileType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    // --- RELATIONSHIPS ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedByUser;

    // This makes the document linkable to a Task.
    // It's nullable because a document might belong to a Case instead.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    // Add more relationships here later as needed:
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "case_id")
    // private Case caseEntity;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public User getUploadedByUser() {
        return uploadedByUser;
    }

    public void setUploadedByUser(User uploadedByUser) {
        this.uploadedByUser = uploadedByUser;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}