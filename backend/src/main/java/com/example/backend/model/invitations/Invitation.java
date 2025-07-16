package com.example.backend.model.invitations;

import com.example.backend.model.AppRole;
import com.example.backend.model.firm.Firm;
import com.example.backend.model.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // The firm that this invitation belongs to.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "firm_id", nullable = false)
    private Firm firm;

    // The lawyer who sent the invitation.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private User invitedByUser;

    // The email address of the person being invited.
    @Column(nullable = false)
    private String email;

    // The role that will be assigned to the user upon successful sign-up.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole roleToAssign;

    // The secure, single-use token sent in the invitation email link.
    @Column(nullable = false, unique = true)
    private String invitationToken;

    // The date and time when this invitation token will no longer be valid.
    @Column(nullable = false)
    private Instant expiresAt;

    // The current status of the invitation.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    // OPTIONAL BUT RECOMMENDED: For inviting clients directly to a case
    @Column(name = "case_id_context")
    private UUID caseIdContext;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    public User getInvitedByUser() {
        return invitedByUser;
    }

    public void setInvitedByUser(User invitedByUser) {
        this.invitedByUser = invitedByUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppRole getRoleToAssign() {
        return roleToAssign;
    }

    public void setRoleToAssign(AppRole roleToAssign) {
        this.roleToAssign = roleToAssign;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public UUID getCaseIdContext() {
        return caseIdContext;
    }

    public void setCaseIdContext(UUID caseIdContext) {
        this.caseIdContext = caseIdContext;
    }
}