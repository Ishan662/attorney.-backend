package com.example.backend.repositories;

import com.example.backend.model.support.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, UUID> {

    /**
     * Finds all messages associated with a single support case,
     * ensuring they are returned in the order they were created.
     * This is crucial for displaying the conversation chronologically.
     *
     * Spring Data JPA will automatically generate the query for this method
     * based on its name.
     *
     * @param supportCaseId The UUID of the parent SupportCase.
     * @return A list of SupportMessage entities.
     */
    List<SupportMessage> findBySupportCaseIdOrderByCreatedAtAsc(UUID supportCaseId);

}