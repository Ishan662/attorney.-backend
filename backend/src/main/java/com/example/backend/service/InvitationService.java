// >> In a new file: InvitationService.java
package com.example.backend.service;

import com.example.backend.dto.FinalizeInvitationRequest;
import com.example.backend.dto.InviteUserRequest;
import com.example.backend.model.*;
import com.example.backend.repositories.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class InvitationService {

    @Autowired private UserRepository userRepository;
    @Autowired private InvitationRepository invitationRepository;
    @Autowired private CaseRepository caseRepository; // Needed to validate case context
    @Autowired private CaseMemberRepository caseMemberRepository; // Needed to add clients to cases

    @Transactional
    public void createAndSendInvitation(InviteUserRequest inviteRequest) {
        // 1. Get the currently authenticated lawyer
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User invitingLawyer = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new IllegalStateException("Authenticated lawyer not found."));

        // 2. Validate the request
        if (userRepository.findByEmail(inviteRequest.getEmail()).filter(u -> u.getStatus() == UserStatus.ACTIVE).isPresent()) {
            throw new IllegalStateException("An active user with this email already exists.");
        }
        if (inviteRequest.getRole() == AppRole.LAWYER || inviteRequest.getRole() == AppRole.ADMIN) {
            throw new IllegalArgumentException("Cannot invite users with this role.");
        }

        // 3. Pre-provision a placeholder user if they don't exist
        User placeholderUser = userRepository.findByEmail(inviteRequest.getEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(inviteRequest.getEmail());
                    newUser.setFirm(invitingLawyer.getFirm());
                    newUser.setRole(inviteRequest.getRole());
                    parseAndSetFullName(newUser, inviteRequest.getFullName()); // set the full name with the help of a helper function
                    newUser.setStatus(UserStatus.PENDING_INVITATION);
                    return userRepository.save(newUser);
                });

        // 4. Create the invitation record
        Invitation invitation = new Invitation();
        invitation.setFirm(invitingLawyer.getFirm());
        invitation.setInvitedByUser(invitingLawyer);
        invitation.setEmail(inviteRequest.getEmail());
        invitation.setRoleToAssign(inviteRequest.getRole());
        invitation.setInvitationToken(UUID.randomUUID().toString());
        invitation.setExpiresAt(Instant.now().plus(48, ChronoUnit.HOURS));

        if (inviteRequest.getRole() == AppRole.CLIENT) {
            // Ensure the case exists and belongs to the lawyer's firm
            caseRepository.findById(inviteRequest.getCaseId())
                    .filter(c -> c.getFirm().getId().equals(invitingLawyer.getFirm().getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid case ID provided."));
            invitation.setCaseIdContext(inviteRequest.getCaseId());
        }

        invitationRepository.save(invitation);

        // 5. TODO: Trigger an external email service here
        // emailService.sendInvitationEmail(invitation.getEmail(), invitation.getInvitationToken());
        System.out.println("SIMULATING EMAIL: Invitation sent for " + invitation.getEmail());
    }

    @Transactional
    public void finalizeInvitation(FinalizeInvitationRequest finalizeRequest) {
        // 1. Find and validate the invitation record
        Invitation invitation = invitationRepository.findByInvitationToken(finalizeRequest.getInvitationToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token."));
        // Further validation...
        if (invitation.getStatus() != InvitationStatus.PENDING || invitation.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Invitation is invalid, used, or expired.");
        }

        // 2. Verify the Firebase token from the newly signed-up user
        FirebaseToken decodedToken;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(finalizeRequest.getFirebaseIdToken());
        } catch (Exception e) { throw new RuntimeException("Invalid Firebase token.", e); }

        // 3. Security Check: Email from invitation must match email from token
        if (!invitation.getEmail().equalsIgnoreCase(decodedToken.getEmail())) {
            throw new SecurityException("Invitation email and token email do not match.");
        }

        // 4. Find the placeholder user and "activate" them
        User userToActivate = userRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new IllegalStateException("Invited user record not found. This should not happen."));

        userToActivate.setFirebaseUid(decodedToken.getUid());
        userToActivate.setFirstName(finalizeRequest.getFirstName());
        userToActivate.setLastName(finalizeRequest.getLastName());
        userToActivate.setStatus(UserStatus.ACTIVE);
        userRepository.save(userToActivate);

        // 5. If it's a client, add them to the case_members table
        if (userToActivate.getRole() == AppRole.CLIENT && invitation.getCaseIdContext() != null) {
            Case kase = caseRepository.findById(invitation.getCaseIdContext()).get(); // We know it exists from the check above
            CaseMember membership = new CaseMember(kase, userToActivate);
            caseMemberRepository.save(membership);
        }

        // 6. Mark the invitation as used to prevent reuse
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    /**
     * A private helper method to parse a full name string into first and last name fields on a User object.
     * @param user The User object to modify.
     * @param fullName The full name string to parse.
     */
    private void parseAndSetFullName(User user, String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return;
        }
        String[] nameParts = fullName.trim().split("\\s+", 2);
        user.setFirstName(nameParts[0]);
        if (nameParts.length > 1) {
            user.setLastName(nameParts[1]);
        } else {
            user.setLastName(""); // Set an empty string if no last name is present
        }
    }
}