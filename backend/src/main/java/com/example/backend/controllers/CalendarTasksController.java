package com.example.backend.controllers;

import com.example.backend.dto.calendarTasksDTOs.CalendarTaskRequestDTO;
import com.example.backend.dto.calendarTasksDTOs.CalendarTasksDTO;
import com.example.backend.dto.calendarTasksDTOs.UpdateCalendarTaskDTO;
import com.example.backend.service.CalendarTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class CalendarTasksController {

    private final CalendarTaskService calendarTaskService;

    public CalendarTasksController(CalendarTaskService calendarTaskService) {
        this.calendarTaskService = calendarTaskService;
    }

    // Get all tasks for the currently authenticated user
    @GetMapping("/my-tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CalendarTasksDTO>> getMyTasks() {
        List<CalendarTasksDTO> tasks = calendarTaskService.getTasksByCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    // Create a new task
    @PostMapping
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<CalendarTasksDTO> createTask(@RequestBody CalendarTaskRequestDTO createDto) {
        CalendarTasksDTO newTask = calendarTaskService.createTask(createDto);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    // Update an existing task
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<CalendarTasksDTO> updateTask(@PathVariable("taskId") UUID taskId,
                                                       @RequestBody UpdateCalendarTaskDTO updateDto) {
        CalendarTasksDTO updatedTask = calendarTaskService.updateTask(taskId, updateDto);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete a task
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<Void> deleteTask(@PathVariable("taskId") UUID taskId) {
        calendarTaskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
