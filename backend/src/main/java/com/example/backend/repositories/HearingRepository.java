package com.example.backend.repositories;

import com.example.backend.model.hearing.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, UUID> {

    List<Hearing> findAllByaCase_IdOrderByHearingDateAsc(UUID caseId);

    List<Hearing> findByLawyer_IdAndStartTimeBetween(UUID lawyerId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Hearing> findByaCase_IdOrderByHearingDateAsc(UUID id);

    default List<Hearing> findByLawyerIdAndDate(UUID lawyerId, LocalDate date) {
        return findByLawyer_IdAndStartTimeBetween(lawyerId, date.atStartOfDay(), date.atTime(23, 59));
    }

    @Query("SELECT h FROM Hearing h WHERE h.lawyer.id = :lawyerId ORDER BY h.startTime ASC")
    List<Hearing> findAllByLawyerId(@Param("lawyerId") UUID lawyerId);

    @Query("""
        SELECT h 
        FROM Hearing h
        JOIN h.aCase c
        JOIN c.members m
        WHERE m.user.id = :clientId
          AND h.hearingDate > CURRENT_TIMESTAMP
        ORDER BY h.hearingDate ASC
    """)
    List<Hearing> findUpcomingHearingsByClientId(@Param("clientId") UUID clientId);



}
