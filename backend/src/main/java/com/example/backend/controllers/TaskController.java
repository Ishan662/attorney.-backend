// >> Path: src/main/java/com/example/backend/controllers/TaskController.java

package com.example.backend.controllers;

import com.example.backend.dto.document.DocumentResponseDTO;
import com.example.backend.dto.taskDTOS.TaskCreateRequestDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.dto.taskDTOS.TaskUpdateRequestDTO;
import com.example.backend.model.document.Document;
import com.example.backend.service.DocumentService;
import com.example.backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final DocumentService documentService;

    public TaskController(TaskService taskService, DocumentService documentService) {
        this.taskService = taskService;
        this.documentService = documentService;
    }

    /**
     * Creates a new task. Must be called by a LAWYER.
     */
    @PostMapping
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskCreateRequestDTO request) {
        TaskResponseDTO task = taskService.createTask(request);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    /**
     * Gets tasks relevant to the current user.
     * - For LAWYERs: returns all tasks in their firm.
     * - For JUNIORs: returns only tasks assigned to them.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<List<TaskResponseDTO>> getTasks() {
        List<TaskResponseDTO> tasks = taskService.getTasksForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Updates the status of a task. Must be called by the JUNIOR assigned to the task.
     */
    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('JUNIOR')")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable UUID taskId,
            @RequestBody TaskUpdateRequestDTO request) {
        TaskResponseDTO updatedTask = taskService.updateTaskByJunior(taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    // >> In: src/main/java/com/example/backend/controllers/TaskController.java
// ... (inside the TaskController class)

    @GetMapping("/{taskId}/documents")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<List<DocumentResponseDTO>> getTaskDocuments(@PathVariable UUID taskId) {
        List<DocumentResponseDTO> documents = documentService.getDocumentsForTask(taskId);
        return ResponseEntity.ok(documents);
    }
}