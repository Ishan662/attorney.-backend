package com.example.backend.service;

import com.example.backend.dto.calendarTasksDTOs.CalendarTaskRequestDTO;
import com.example.backend.dto.calendarTasksDTOs.CalendarTasksDTO;
import com.example.backend.dto.calendarTasksDTOs.UpdateCalendarTaskDTO;
import com.example.backend.model.CalendarTasks.CalendarTasks;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CalendarTasksRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CalendarTaskService {

    private final CalendarTasksRepository taskRepository;
    private final UserRepository userRepository;

    public CalendarTaskService(CalendarTasksRepository taskRepository,
                               UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all tasks for the currently authenticated user.
     */
    public List<CalendarTasksDTO> getTasksByCurrentUser() {
        User currentUser = getCurrentUser();
        List<CalendarTasks> tasks = taskRepository.findByLawyer(currentUser);
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new task for the logged-in user.
     */
    @Transactional
    public CalendarTasksDTO createTask(CalendarTaskRequestDTO createDto) {
        User currentUser = getCurrentUser();

        CalendarTasks task = new CalendarTasks();
        task.setLawyer(currentUser);
        task.setTitle(createDto.getTitle());
        task.setDescription(createDto.getDescription());
        task.setLocation(createDto.getLocation());
        task.setStartTime(createDto.getStartTime());
        task.setEndTime(createDto.getEndTime());
        task.setPriority(createDto.getPriority());
        task.setStatus(createDto.getStatus());

        CalendarTasks saved = taskRepository.save(task);
        return toDTO(saved);
    }

    /**
     * Update an existing task.
     */
    @Transactional
    public CalendarTasksDTO updateTask(UUID id, UpdateCalendarTaskDTO updateDto) {
        User currentUser = getCurrentUser();

        CalendarTasks task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // ensure only owner can update
        if (!task.getLawyer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You do not have permission to update this task");
        }

        task.setTitle(updateDto.getTitle());
        task.setDescription(updateDto.getDescription());
        task.setLocation(updateDto.getLocation());
        task.setStartTime(updateDto.getStartTime());
        task.setEndTime(updateDto.getEndTime());
        task.setPriority(updateDto.getPriority());
        task.setStatus(updateDto.getStatus());

        CalendarTasks updated = taskRepository.save(task);
        return toDTO(updated);
    }

    /**
     * Delete a task by ID.
     */
    @Transactional
    public void deleteTask(UUID id) {
        User currentUser = getCurrentUser();

        CalendarTasks task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getLawyer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You do not have permission to delete this task");
        }

        taskRepository.delete(task);
    }

    // --- helper: map entity → DTO ---
    private CalendarTasksDTO toDTO(CalendarTasks task) {
        CalendarTasksDTO dto = new CalendarTasksDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setLocation(task.getLocation());
        dto.setStartTime(task.getStartTime());
        dto.setEndTime(task.getEndTime());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }

    // --- helper: get current authenticated user ---
    public User getCurrentUser() {
        String firebaseUid =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
