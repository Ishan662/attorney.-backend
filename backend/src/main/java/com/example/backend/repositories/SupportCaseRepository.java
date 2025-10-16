package com.example.backend.repositories;

import com.example.backend.model.support.SupportCase;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportCaseRepository extends JpaRepository<SupportCase, UUID>, JpaSpecificationExecutor<SupportCase> {
    List<SupportCase> findByCreatedByUserIdOrderByCreatedAtDesc(UUID userId);
}