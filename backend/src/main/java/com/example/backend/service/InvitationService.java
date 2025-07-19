// >> In a new file: InvitationService.java
package com.example.backend.service;

import com.example.backend.dto.InviteDTOS.FinalizeInvitationRequest;
import com.example.backend.dto.InviteDTOS.InvitationDetailsDTO;
import com.example.backend.dto.InviteDTOS.InviteUserRequest;
import com.example.backend.model.*;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.invitations.Invitation;
import com.example.backend.model.invitations.InvitationStatus;
import com.example.backend.model.user.User;
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
    @Autowired private EmailService emailService;

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

        if (inviteRequest.getRole() == AppRole.CLIENT) {
            if (inviteRequest.getCaseId() == null) {
                throw new IllegalArgumentException("Case ID is required when inviting a client.");
            }

            // 1. Find the case and verify the lawyer has permission to edit it.
            Case targetCase = caseRepository.findById(inviteRequest.getCaseId())
                    .filter(c -> c.getFirm().getId().equals(invitingLawyer.getFirm().getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Case not found or you do not have permission."));

            // 2. Update the case with the new client information from the form.
            targetCase.setClientName(inviteRequest.getFullName());
            targetCase.setClientEmail(inviteRequest.getEmail());
            targetCase.setClientPhone(inviteRequest.getPhoneNumber()); // Assuming DTO has this

            // 3. Save the updated case details.
            caseRepository.save(targetCase);
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
                    newUser.setPhoneNumber(inviteRequest.getPhoneNumber());
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
        System.out.println("URL: http://localhost:5173/accept-invitation/"+invitation.getInvitationToken());
        String recipientEmail = invitation.getEmail();
        String subject = String.format("Invitation to Join %s on The Attorney Platform", invitation.getFirm().getFirmName());

        String emailBody = getString(invitation);

        // Call our new service to dispatch the email!
        emailService.sendSimpleMessage(recipientEmail, subject, emailBody);
    }

    // Function for sending the email
    private static String getString(Invitation invitation) {
        String invitationUrl = "http://localhost:5173/accept-invitation/" + invitation.getInvitationToken();

        String emailBody = String.format(
                "Hello,\n\nYou have been personally invited by %s to join their team on The Attorney Platform.\n\n" +
                        "Please click the link below to accept your invitation and create your secure account:\n\n" +
                        "%s\n\n" +
                        "This link is valid for 48 hours.\n\n" +
                        "If you were not expecting this invitation, you can safely disregard this email.\n\n" +
                        "Sincerely,\nThe Attorney Platform Team",
                invitation.getInvitedByUser().getFirstName(), // Get the lawyer's name who sent the invite
                invitationUrl
        );
        return emailBody;
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
        userToActivate.setStatus(UserStatus.PENDING_PHONE_VERIFICATION);
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

    public InvitationDetailsDTO getInvitationDetails(String token) {
        // Find the invitation and ensure it's still valid
        Invitation invitation = invitationRepository.findByInvitationToken(token)
                .filter(inv -> inv.getStatus() == InvitationStatus.PENDING && inv.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired invitation token."));

        // Find the pre-provisioned user to get their name
        User user = userRepository.findByEmail(invitation.getEmail())
                .orElseThrow(() -> new IllegalStateException("Placeholder user not found for invitation."));

        InvitationDetailsDTO dto = new InvitationDetailsDTO();
        dto.setEmail(user.getEmail());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String fullName = user.getFirstName() + " " + user.getLastName();
        dto.setFullName(fullName);
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
}