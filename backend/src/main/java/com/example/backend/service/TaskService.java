package com.example.backend.service;

import com.example.backend.dto.taskDTOS.CreateTaskRequestDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.dto.taskDTOS.UpdateTaskRequestDTO;
import com.example.backend.mapper.TaskMapper;
import com.example.backend.model.tasks.Task;
import com.example.backend.model.tasks.TaskPriority;
import com.example.backend.model.tasks.TaskStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException; // Import this
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
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }

    public TaskResponseDTO assignTask(CreateTaskRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = authentication.getName();

        // 1. Get the User object for the person assigning the task (from context)
        User assignedByUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found with Firebase UID: " + firebaseUid));

        // 2. Fetch and VALIDATE the User object for the person the task is being assigned TO
        User assignedToUser = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new EntityNotFoundException("User to assign task to not found with ID: " + request.getAssignedToUserId()));

        // 3. Create a new Task entity using the full User objects
        Task newTask = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .status(TaskStatus.PENDING)
                .assignedByUser(assignedByUser) // Set the User object
                .assignedToUser(assignedToUser) // Set the validated User object
                .priority(TaskPriority.LAW) // Make sure to handle this if needed
                .build();

        // Save the new Task
        Task savedTask = taskRepository.save(newTask);

        return taskMapper.toDto(savedTask);
    }

    public List<TaskResponseDTO> getTasksByCaseId(UUID caseId) {
        List<Task> tasks = taskRepository.findAllByCaseId(caseId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO updateTask(UpdateTaskRequestDTO request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + request.getTaskId()));

        // Only allow the assigned user (Junior Lawyer) to update their own task
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = auth.getName();
        if (!task.getAssignedToUser().getFirebaseUid().equals(firebaseUid)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        // Add documents
        if (request.getDocumentUrls() != null) {
            task.setDocumentUrls(request.getDocumentUrls());
        }

        // Mark task as completed
        task.setCompleted(request.isCompleted());

        Task updatedTask = taskRepository.save(task);

        return taskMapper.toDto(updatedTask);
    }
}