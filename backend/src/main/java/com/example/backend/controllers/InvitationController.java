package com.example.backend.controllers;


import com.example.backend.dto.InviteUserRequest;
import com.example.backend.model.Invitation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService){
        this.invitationService = invitationService;
    }

    /*
     * Endpoint for a Lawyer to create and send a new invitation.
     */
    @PostMapping()
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Void> createInvitation(@RequestBody InviteUserRequest inviteRequest){
        // The service layer will handle all the logic of creating the placeholder user,
        // the invitation record, and sending the email.
        invitationService.createAndSendInvitation(inviteRequest);
        return ResponseEntity.accepted().build();
    }

    /*
     * Public endpoint for an invited user to finalize their registration.
     * This links their new Firebase account to their pre-provisioned user record.
     */
    @PostMapping("/finalize")
    public ResponseEntity<Void> finalizeInvitation(@RequestBody FinalizeInvitationRequest finalizeRequest) {
        invitationService.finalizeInvitation(finalizeRequest);
        return ResponseEntity.ok().build();
    }

}
