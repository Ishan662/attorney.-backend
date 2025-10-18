package com.example.backend.repositories;

import com.example.backend.model.lawyer.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {
    Optional<Lawyer> findByUserId(UUID userId);
}
