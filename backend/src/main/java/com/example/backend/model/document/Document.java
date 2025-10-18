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
}