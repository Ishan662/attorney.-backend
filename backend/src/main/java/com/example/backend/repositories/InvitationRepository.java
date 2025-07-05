package com.example.backend.repositories;

import com.example.backend.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface  InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByInvitationId(UUID invitationId);
}
