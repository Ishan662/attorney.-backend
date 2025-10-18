package com.example.backend.mapper;

import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.model.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponseDTO toDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setAssignedByUser(task.getAssignedByUser() != null ? task.getAssignedByUser().getId() : null);
        dto.setAssignedToUser(task.getAssignedToUser() != null ? task.getAssignedToUser().getId() : null);
        dto.setCaseId(task.getCaseId());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);

        return dto;
    }
}
