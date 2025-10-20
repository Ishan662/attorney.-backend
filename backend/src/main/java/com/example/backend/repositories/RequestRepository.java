// >> Path: src/main/java/com/example/backend/repositories/RequestRepository.java

package com.example.backend.repositories;

import com.example.backend.model.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    /**
     * Finds all meeting requests created by a specific client.
     */
    List<Request> findByCreatedByClient_Id(UUID clientId);

    /**
     * Finds all meeting requests directed to a specific lawyer.
     */
    List<Request> findByRequestedLawyer_Id(UUID lawyerId);

    /**
     * Finds potentially overlapping meeting requests for a specific lawyer on a given date.
     * This checks if a new request's time range [newStart, newEnd] overlaps with any
     * existing, non-declined request's time range [existingStart, existingEnd].
     * Overlap condition: (existingStart < newEnd) AND (existingEnd > newStart).
     */
    @Query("SELECT r FROM Request r WHERE r.requestedLawyer.id = :lawyerId AND r.meetingDate = :date AND r.startTime < :endTime AND r.endTime > :startTime AND r.status <> 'DECLINED'")
    List<Request> findOverlappingRequestsForLawyer(
            @Param("lawyerId") UUID lawyerId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    List<Request> findAllByRequestedLawyerId(UUID lawyerId);
}