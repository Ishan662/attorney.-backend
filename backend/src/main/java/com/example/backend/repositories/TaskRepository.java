package com.example.backend.repositories;

import com.example.backend.model.tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Finds all tasks for a specific case within a specific firm (secure)
    List<Task> findByFirmIdAndCaseId(UUID firmId, UUID caseId);

    // Finds all tasks belonging to a specific firm
    List<Task> findByFirmId(UUID firmId);

    // Finds all tasks assigned to a specific user (for juniors)
    List<Task> findByAssignedToUser_Id(UUID userId);

    // --- ADD THIS NEW METHOD ---
    @Query("SELECT t FROM Task t JOIN FETCH t.firm WHERE t.id = :taskId")
    Optional<Task> findByIdWithFirm(@Param("taskId") UUID taskId);
}