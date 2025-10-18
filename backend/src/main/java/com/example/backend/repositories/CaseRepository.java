package com.example.backend.repositories;

import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.Instant;

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

    boolean existsByFirmIdAndCaseNumber(UUID firmId, String caseNumber);

//    /**
//     * Finds cases for a lawyer with dynamic filtering.
//     * JPQL handles the null checks, making the query clean.
//     */
    @Query("SELECT c FROM Case c WHERE c.firm.id = :firmId " +
            "AND (LOWER(c.caseTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(c.caseNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     :searchTerm IS NULL OR :searchTerm = '') " +
            "AND (:caseType IS NULL OR c.caseType = :caseType) " +
            "AND (:court IS NULL OR c.courtName = :court) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "ORDER BY c.createdAt DESC")
    List<Case> findCasesForLawyerWithFilters(
            @Param("firmId") UUID firmId,
            @Param("searchTerm") String searchTerm,
            @Param("caseType") String caseType,
            @Param("court") String court,
            @Param("status") CaseStatus status
            // Note: Date filtering in JPQL is more complex, let's add it later if needed.
    );


    /**
     * Finds cases for a junior/client with dynamic filtering.
     * The only difference is the added JOIN to case_members.
     */
    @Query("SELECT c FROM Case c JOIN c.members m WHERE m.user.id = :userId " +
            "AND (LOWER(c.caseTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(c.caseNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     :searchTerm IS NULL OR :searchTerm = '') " +
            "AND (:caseType IS NULL OR c.caseType = :caseType) " +
            "AND (:court IS NULL OR c.courtName = :court) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "ORDER BY c.createdAt DESC")
    List<Case> findCasesForMemberWithFilters(
            @Param("userId") UUID userId,
            @Param("searchTerm") String searchTerm,
            @Param("caseType") String caseType,
            @Param("court") String court,
            @Param("status") CaseStatus status
    );

//    @Query("SELECT DISTINCT c FROM Case c LEFT JOIN c.hearings h " +
//            "WHERE c.firm.id = :firmId " +
//            "AND (:searchTerm IS NULL OR " +
//            "     FUNCTION('lower', c.caseTitle) LIKE FUNCTION('lower', CONCAT('%', :searchTerm, '%')) OR " +
//            "     FUNCTION('lower', c.caseNumber) LIKE FUNCTION('lower', CONCAT('%', :searchTerm, '%'))) " +
//            "AND (:caseType IS NULL OR c.caseType = :caseType) " +
//            "AND (:court IS NULL OR c.courtName = :court) " +
//            "AND (:status IS NULL OR c.status = :status) " +
//            "AND ((:startDate IS NULL AND :endDate IS NULL) OR EXISTS (" +
//            "     SELECT 1 FROM Hearing h2 WHERE h2.aCase = c AND h2.hearingDate IS NOT NULL " +
//            "     AND (:startDate IS NULL OR h2.hearingDate >= :startDate) " +
//            "     AND (:endDate IS NULL OR h2.hearingDate <= :endDate)" +
//            "     )) " +
//            "ORDER BY c.createdAt DESC")
//    List<Case> findCasesForLawyerWithFilters(
//            @Param("firmId") UUID firmId,
//            @Param("searchTerm") String searchTerm,
//            @Param("caseType") String caseType,
//            @Param("court") String court,
//            @Param("status") CaseStatus status,
//            @Param("startDate") Instant startDate,
//            @Param("endDate") Instant endDate
//    );
//
//    @Query("SELECT DISTINCT c FROM Case c JOIN c.members m " +
//            "WHERE m.user.id = :userId " +
//            "AND (:searchTerm IS NULL OR " +
//            "     FUNCTION('lower', c.caseTitle) LIKE FUNCTION('lower', CONCAT('%', :searchTerm, '%')) OR " +
//            "     FUNCTION('lower', c.caseNumber) LIKE FUNCTION('lower', CONCAT('%', :searchTerm, '%'))) " +
//            "AND (:caseType IS NULL OR c.caseType = :caseType) " +
//            "AND (:court IS NULL OR c.courtName = :court) " +
//            "AND (:status IS NULL OR c.status = :status) " +
//            "AND ((:startDate IS NULL AND :endDate IS NULL) OR EXISTS (" +
//            "     SELECT 1 FROM Hearing h WHERE h.aCase = c AND h.hearingDate IS NOT NULL " +
//            "     AND (:startDate IS NULL OR h.hearingDate >= :startDate) " +
//            "     AND (:endDate IS NULL OR h.hearingDate <= :endDate)" +
//            "     )) " +
//            "ORDER BY c.createdAt DESC")
//    List<Case> findCasesForMemberWithFilters(
//            @Param("userId") UUID userId,
//            @Param("searchTerm") String searchTerm,
//            @Param("caseType") String caseType,
//            @Param("court") String court,
//            @Param("status") CaseStatus status,
//            @Param("startDate") Instant startDate,
//            @Param("endDate") Instant endDate
//    );

    @Query("SELECT c FROM Case c JOIN c.members m WHERE m.user.id = :userId")
    List<Case> findCasesByUserId(@Param("userId") UUID userId);
}