package com.example.backend.payment.repository;

import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
