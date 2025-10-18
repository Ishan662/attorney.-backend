package com.example.backend.dto.taskDTOS;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskDocumentDTO {
    private UUID id;
    private String fileName;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private String uploadedBy; // User's full name
}
