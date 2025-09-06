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

        TaskResponseDTO dto = TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedByUser(task.getAssignedByUser() != null ? task.getAssignedByUser().getId() : null)
                .assignedToUser(task.getAssignedToUser() != null ? task.getAssignedToUser().getId() : null)
                .caseId(task.getCaseId())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .build();

        return dto;
    }
}