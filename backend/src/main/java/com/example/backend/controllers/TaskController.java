package com.example.backend.controllers;

import com.example.backend.dto.taskDTOS.CreateTaskRequestDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.dto.taskDTOS.UpdateTaskRequestDTO;
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

    public TaskController(TaskService taskService)
    {
        this.taskService = taskService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('LAWYER')")

    public ResponseEntity<TaskResponseDTO> assignTask(
            @RequestBody CreateTaskRequestDTO request)
    {
        TaskResponseDTO task = taskService.assignTask(request);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @GetMapping("/{caseId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<List<TaskResponseDTO>> getTasksForCase(@PathVariable UUID caseId) {
        return ResponseEntity.ok(taskService.getTasksByCaseId(caseId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('JUNIOR')")
    public ResponseEntity<TaskResponseDTO> updateTask(@RequestBody UpdateTaskRequestDTO request) {
        TaskResponseDTO updatedTask = taskService.updateTask(request);
        return ResponseEntity.ok(updatedTask);
    }



}
