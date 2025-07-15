package com.example.backend.dto.InviteDTOS;

public class FinalizeInvitationRequest {
    private String invitationToken;
    private String firebaseIdToken;
    private String firstName;
    private String lastName;

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public String getFirebaseIdToken() {
        return firebaseIdToken;
    }

    public void setFirebaseIdToken(String firebaseIdToken) {
        this.firebaseIdToken = firebaseIdToken;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
