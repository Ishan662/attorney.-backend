// >> Create this new file: dto/InvitationDetailsDTO.java
package com.example.backend.dto.InviteDTOS;

public class InvitationDetailsDTO {
    private String email;
    private String fullName; // We can get this from the placeholder user record

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
