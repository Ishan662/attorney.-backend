package com.example.backend.model;

    public enum UserStatus {
        PENDING_INVITATION, // User has been invited but has not created an account yet.
        ACTIVE,             // User has completed sign-up and can log in.
        INACTIVE            // User was active but has been disabled by a lawyer or admin.
    }
