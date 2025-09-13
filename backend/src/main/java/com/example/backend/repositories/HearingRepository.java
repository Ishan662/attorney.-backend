package com.example.backend.repositories;

// Import the Hearing entity from its new package
import com.example.backend.model.hearing.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, UUID> {
    List<Hearing> findAllByaCase_IdOrderByHearingDateAsc(UUID caseId);

    List<Hearing> findByLawyerIdAndStartTimeBetween(UUID lawyerId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Hearing> findByaCase_IdOrderByHearingDateAsc(UUID id);

    default List<Hearing> findByLawyerIdAndDate(UUID lawyerId, LocalDate date) {
        return findByLawyerIdAndStartTimeBetween(lawyerId, date.atStartOfDay(), date.atTime(23,59));
    }
}