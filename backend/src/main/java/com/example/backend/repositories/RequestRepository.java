package com.example.backend.repositories;

import com.example.backend.model.requests.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {
    // Finds all requests for a given case, ordered by requestedDate ascending
    List<Request> findByaCase_IdOrderByRequestedDateAsc(UUID caseId);
}

