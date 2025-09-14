package com.example.backend.repositories;

import com.example.backend.model.tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByCaseId(UUID caseId);
}
