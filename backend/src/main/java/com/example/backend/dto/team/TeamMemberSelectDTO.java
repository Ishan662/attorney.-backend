package com.example.backend.dto.team;

import java.util.UUID;

/**
 * A lightweight DTO for populating selection dropdowns on the frontend.
 * Contains only the essential information needed to display and identify a user.
 */
public class TeamMemberSelectDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    // No-arg constructor
    public TeamMemberSelectDTO() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}