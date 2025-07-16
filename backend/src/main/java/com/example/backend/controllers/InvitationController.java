package com.example.backend.controllers;


import com.example.backend.dto.InviteDTOS.FinalizeInvitationRequest;
import com.example.backend.dto.InviteDTOS.InvitationDetailsDTO;
import com.example.backend.dto.InviteDTOS.InviteUserRequest;
import com.example.backend.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/create-invitation")
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

    // ▼▼▼ ADD THIS NEW ENDPOINT ▼▼▼
    /**
     * A public endpoint to get the basic details of a pending invitation.
     * This allows the frontend to pre-fill the invitee's email and name.
     * @param token The invitation token from the URL.
     * @return A DTO with safe-to-display information.
     */
    @GetMapping("/details")
    public ResponseEntity<InvitationDetailsDTO> getInvitationDetails(@RequestParam String token) {
        InvitationDetailsDTO details = invitationService.getInvitationDetails(token);
        return ResponseEntity.ok(details);
    }

}
