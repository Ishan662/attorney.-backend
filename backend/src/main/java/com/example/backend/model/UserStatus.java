package com.example.backend.model;

    public enum UserStatus {
        PENDING_INVITATION, // User has been invited but has not created an account yet.
        PENDING_PHONE_VERIFICATION, // still phone number is not verified.
        ACTIVE,             // User has completed sign-up and can log in.
        INACTIVE            // User was active but has been disabled by a lawyer or admin.
    }
