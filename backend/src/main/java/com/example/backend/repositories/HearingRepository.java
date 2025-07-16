package com.example.backend.repositories;

// Import the Hearing entity from its new package
import com.example.backend.model.hearing.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, UUID> {

    // This method name tells Spring Data JPA to:
    // "Find all Hearing entities...
    // where the 'aCase' object's 'id' field matches the given caseId...
    // and order the results by the 'hearingDate' field in ascending order."
    List<Hearing> findAllByaCase_IdOrderByHearingDateAsc(UUID caseId);
}