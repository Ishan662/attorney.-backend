// >> Path: src/main/java/com/example/backend/dto/document/DocumentResponseDTO.java

package com.example.backend.dto.document;

import com.example.backend.dto.taskDTOS.TaskResponseDTO; // Assuming UserInfoDTO is here
import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentResponseDTO {

    private UUID id;
    private String originalFileName;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private TaskResponseDTO.UserInfoDTO uploadedByUser;

    public DocumentResponseDTO() {}

    // --- GETTERS AND SETTERS ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public TaskResponseDTO.UserInfoDTO getUploadedByUser() { return uploadedByUser; }
    public void setUploadedByUser(TaskResponseDTO.UserInfoDTO uploadedByUser) { this.uploadedByUser = uploadedByUser; }
}