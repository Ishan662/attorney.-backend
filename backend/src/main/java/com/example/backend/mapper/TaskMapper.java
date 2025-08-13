package com.example.backend.mapper;

import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.model.tasks.Task;
import org.springframework.stereotype.Component;

@Component // Make this a Spring-managed bean for dependency injection
public class TaskMapper {

    public TaskResponseDTO toDto(Task task) {
        if(task == null) {
            return null;
        }

        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setAssignedByUserId(task.getAssignedByUserId());
        dto.setAssignedToUserId(task.getAssignedToUserId());
        dto.setCaseId(task.getCaseId());

        // Ensure you handle the TaskStatus enum correctly
        if (task.getStatus() != null) {
            dto.setStatus(task.getStatus().name());
        }

        return dto;
    }
}