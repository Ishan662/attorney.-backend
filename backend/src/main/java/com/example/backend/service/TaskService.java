// >> Path: src/main/java/com/example/backend/service/TaskService.java

package com.example.backend.service;

import com.example.backend.dto.taskDTOS.TaskCreateRequestDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.dto.taskDTOS.TaskUpdateRequestDTO;
import com.example.backend.model.AppRole;
import com.example.backend.model.tasks.Task;
import com.example.backend.model.tasks.TaskPriority;
import com.example.backend.model.tasks.TaskStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // --- CONSTRUCTOR: REMOVED TaskMapper ---
    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponseDTO createTask(TaskCreateRequestDTO request) {
        User lawyer = getCurrentUser();

        User junior = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new EntityNotFoundException("Junior to assign task to not found with ID: " + request.getAssignedToUserId()));

        // --- SECURITY CHECK ---
        if (lawyer.getFirm() == null || !lawyer.getFirm().getId().equals(junior.getFirm().getId())) {
            throw new SecurityException("Forbidden: You can only assign tasks to juniors within your own firm.");
        }
        if (junior.getRole() != AppRole.JUNIOR) {
            throw new IllegalArgumentException("Tasks can only be assigned to users with the JUNIOR role.");
        }

        Task newTask = new Task();
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setType(request.getType());
        newTask.setDueDate(request.getDueDate());
        newTask.setCaseId(request.getCaseId());
        newTask.setStatus(TaskStatus.PENDING);
        newTask.setPriority(TaskPriority.MEDIUM);
        newTask.setAssignedByUser(lawyer);
        newTask.setAssignedToUser(junior);
        newTask.setFirm(lawyer.getFirm());

        Task savedTask = taskRepository.save(newTask);

        // --- CONVERSION LOGIC IS NOW HERE ---
        return convertToDto(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Task> tasks;

        if (currentUser.getRole() == AppRole.LAWYER) {
            tasks = taskRepository.findByFirmId(currentUser.getFirm().getId());
        } else if (currentUser.getRole() == AppRole.JUNIOR) {
            tasks = taskRepository.findByAssignedToUser_Id(currentUser.getId());
        } else {
            return List.of();
        }
        // --- CONVERSION LOGIC IS NOW HERE ---
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByCaseId(UUID caseId) {
        User currentUser = getCurrentUser();
        // Securely find tasks for a case only within the user's firm
        List<Task> tasks = taskRepository.findByFirmIdAndCaseId(currentUser.getFirm().getId(), caseId);
        return tasks.stream().map(this::convertToDto).collect(Collectors.toList());
    }


    @Transactional
    public TaskResponseDTO updateTaskByJunior(UUID taskId, TaskUpdateRequestDTO request) {
        User junior = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        // --- SECURITY CHECK ---
        if (!task.getAssignedToUser().getId().equals(junior.getId())) {
            throw new SecurityException("Forbidden: You can only update tasks that are assigned to you.");
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        // --- CONVERSION LOG-IC IS NOW HERE ---
        return convertToDto(updatedTask);
    }

    // --- HELPER METHODS ---
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found.");
        }
        String firebaseUid = authentication.getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found in database."));
    }

    // --- THIS PRIVATE METHOD REPLACES THE MAPPER ---
    private TaskResponseDTO convertToDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setCaseId(task.getCaseId());

        // Map the assignedByUser to its DTO
        if (task.getAssignedByUser() != null) {
            TaskResponseDTO.UserInfoDTO assignedByDto = new TaskResponseDTO.UserInfoDTO();
            assignedByDto.setId(task.getAssignedByUser().getId());
            assignedByDto.setFirstName(task.getAssignedByUser().getFirstName());
            assignedByDto.setLastName(task.getAssignedByUser().getLastName());
            dto.setAssignedByUser(assignedByDto);
        }

        // Map the assignedToUser to its DTO
        if (task.getAssignedToUser() != null) {
            TaskResponseDTO.UserInfoDTO assignedToDto = new TaskResponseDTO.UserInfoDTO();
            assignedToDto.setId(task.getAssignedToUser().getId());
            assignedToDto.setFirstName(task.getAssignedToUser().getFirstName());
            assignedToDto.setLastName(task.getAssignedToUser().getLastName());
            dto.setAssignedToUser(assignedToDto);
        }

        return dto;
    }
}