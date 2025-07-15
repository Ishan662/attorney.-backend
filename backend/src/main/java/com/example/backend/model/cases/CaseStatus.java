package com.example.backend.model.cases;

    public enum CaseStatus {
        OPEN,           // A new case, not yet actively worked on.
        IN_PROGRESS,    // Actively being worked on.
        ON_HOLD,        // Stalled, perhaps waiting for client documents.
        CLOSED,         // The case is successfully completed.
        ARCHIVED        // "Soft deleted". Hidden from normal views.
    }
