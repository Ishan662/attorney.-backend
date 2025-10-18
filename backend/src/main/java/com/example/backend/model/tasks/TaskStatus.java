package com.example.backend.model.tasks;

public enum TaskStatus {
    PENDING,        // Newly assigned, not yet started by the junior
    IN_PROGRESS,    // Junior has acknowledged and started the work
    COMPLETED,      // Junior has finished, awaiting lawyer's review
    APPROVED,       // Lawyer has reviewed and approved the work, task is closed
    REOPENED        // Lawyer reviewed and sent it back for changes
}