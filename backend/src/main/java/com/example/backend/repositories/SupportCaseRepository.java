package com.example.backend.repositories;

import com.example.backend.model.support.SupportCase;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportCaseRepository extends JpaRepository<SupportCase, UUID>, JpaSpecificationExecutor<SupportCase> {
    List<SupportCase> findByCreatedByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("""
        SELECT sc 
        FROM SupportCase sc
        JOIN FETCH sc.createdByUser u
        WHERE u.role = com.example.backend.model.AppRole.LAWYER
        ORDER BY sc.createdAt DESC
    """)
    List<SupportCase> findAllSupportCasesByLawyers();
}