package com.example.backend.service;

import com.example.backend.dto.taskDTOS.CreateTaskRequestDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.mapper.TaskMapper;
import com.example.backend.model.tasks.Task;
import com.example.backend.model.tasks.TaskStatus;
import com.example.backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponseDTO assignTask(UUID caseId, CreateTaskRequestDTO request) {
        // Get the UUID of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID assignedByUserId = UUID.fromString(authentication.getName());

        // Create a new Task entity using data from the DTO and server-side values
        Task newTask = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedToUserId(request.getAssignedToUserId())
                .assignedByUserId(assignedByUserId)
                .caseId(caseId)
                .status(TaskStatus.PENDING) // Set the initial status to PENDING
                .build();

        // Save the new Task and get the saved instance with the generated ID
        Task savedTask = taskRepository.save(newTask);
        System.out.println("hello" + savedTask.getId());

        // Map the saved Task entity back to a response DTO and return it
//        return TaskResponseDTO.builder()
//                .id(savedTask.getId())
//                .title(savedTask.getTitle())
//                .description(savedTask.getDescription())
//                .assignedByUserId(savedTask.getAssignedByUserId())
//                .assignedToUserId(savedTask.getAssignedToUserId())
//                .caseId(savedTask.getCaseId())
//                .status(savedTask.getStatus().name())
//                .build();
        TaskResponseDTO t = null;
        return t;
    }

    public List<TaskResponseDTO> getTasksByCaseId(UUID caseId) {

        List<Task> tasks = taskRepository.findAllByCaseId(caseId);

        // This line now correctly uses the toDto method from the corrected mapper
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());


    }
}