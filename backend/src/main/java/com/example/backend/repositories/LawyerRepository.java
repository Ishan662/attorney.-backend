package com.example.backend.repositories;

import com.example.backend.model.lawyer.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {
    Optional<Lawyer> findByUserId(UUID userId);
}
