package com.example.backend.payment.repository;

import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment findBySessionId(String sessionId);

    /**
     * Calculates the sum of all successful payment amounts for a given case.
     * @param caseId The UUID of the case.
     * @param status The status to filter by (e.g., SUCCESS).
     * @return The total sum of amounts in the smallest currency unit (e.g., cents), or 0 if none.
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.caseEntity.id = :caseId AND p.status = :status")
    Long sumSuccessfulPaymentsByCaseId(@Param("caseId") UUID caseId, @Param("status") PaymentStatus status);

    /**
     * Finds all payments for cases belonging to a specific firm.
     * @param firmId The UUID of the firm.
     * @return A list of payments.
     */
    @Query("SELECT p FROM Payment p WHERE p.caseEntity.firm.id = :firmId ORDER BY p.createdAt DESC")
    List<Payment> findAllByFirmId(@Param("firmId") UUID firmId);

    @Query("SELECT p FROM Payment p WHERE p.caseEntity.firm.id = :firmId " +
            "AND p.status <> com.example.backend.payment.model.PaymentStatus.SUCCESS " +
            "AND p.caseEntity.createdAt < :cutoffDate") // <-- Use the case's creation date
    List<Payment> findOverdueByFirmId(@Param("firmId") UUID firmId, @Param("cutoffDate") Instant cutoffDate); // <-- Use Instant


    @Query("SELECT p FROM Payment p " +
            "WHERE p.caseEntity.firm.id = :firmId " +
            "AND p.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByFirmForDay(
            @Param("firmId") UUID firmId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

}
