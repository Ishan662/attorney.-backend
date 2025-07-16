package com.example.backend.repositories;

import com.example.backend.model.cases.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Case, UUID> {

    /**
     * Finds all cases belonging to a specific firm.
     * This is primarily for the LAWYER role.
     */
    List<Case> findAllByFirmId(UUID firmId);

    /**
     * Finds all cases that a specific user is a member of.
     * This is the secure way to fetch cases for JUNIORs and CLIENTs.
     * It works by looking at the 'members' collection inside the Case entity and
     * checking the 'id' of the user within that collection.
     */
    @Query("SELECT c FROM Case c JOIN c.members m WHERE m.user.id = :userId")
    List<Case> findCasesByMemberUserId(@Param("userId") UUID userId);
}