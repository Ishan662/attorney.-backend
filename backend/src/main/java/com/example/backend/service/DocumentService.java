package com.example.backend.service;

import com.example.backend.dto.document.DocumentResponseDTO;
import com.example.backend.dto.taskDTOS.TaskResponseDTO;
import com.example.backend.model.document.Document;
import com.example.backend.model.tasks.Task;
import com.example.backend.model.user.User;
import com.example.backend.repositories.DocumentRepository;
import com.example.backend.repositories.TaskRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository, TaskRepository taskRepository, FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Document uploadDocumentForTask(UUID taskId, MultipartFile file) {
        // 1. Identify the current user
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found."));

        // 2. Find the parent task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        // 3. !! CRITICAL SECURITY CHECK !!
        // Can this user upload a document to this task?
        // They must be in the same firm AND either the assigner or assignee.
        if (!currentUser.getFirm().getId().equals(task.getFirm().getId()) ||
                (!currentUser.getId().equals(task.getAssignedByUser().getId()) && !currentUser.getId().equals(task.getAssignedToUser().getId()))) {
            throw new SecurityException("Forbidden: You do not have permission to upload documents to this task.");
        }

        // 4. Store the physical file and get its unique name
        String storedFileName = fileStorageService.storeFile(file);

        // 5. Create and save the document metadata record
        Document document = new Document();
        document.setOriginalFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        document.setStoredFileName(storedFileName);
        document.setFileSize(file.getSize());
        document.setFileType(file.getContentType());
        document.setUploadedByUser(currentUser);
        document.setTask(task); // Link it to the task

        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public Resource getDocumentResource(UUID documentId) {
        // 1. Identify the current user
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found."));

        // 2. Find the document metadata in the database
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with ID: " + documentId));

        // 3. !! CRITICAL SECURITY CHECK !!
        // Can this user view this document?
        // Check permissions based on the parent task
        if (document.getTask() != null) {
            Task parentTask = document.getTask();
            if (!currentUser.getFirm().getId().equals(parentTask.getFirm().getId())) {
                throw new SecurityException("Forbidden: You do not have permission to download this document.");
            }
        }
        // Add more checks here for cases, etc.

        // 4. If authorized, load the physical file from disk
        return fileStorageService.loadFileAsResource(document.getStoredFileName());
    }

    // This is a helper method to get the current user safely.
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = authentication.getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found."));
    }

    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getDocumentsForTask(UUID taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: ".concat(taskId.toString())));

        // Security Check
        if (!currentUser.getFirm().getId().equals(task.getFirm().getId())) {
            throw new SecurityException("Forbidden: You do not have permission to view documents for this task.");
        }

        List<Document> documents = documentRepository.findByTaskId(taskId);

        // Convert the list of entities to a list of DTOs before returning
        return documents.stream()
                .map(this::convertDocumentToDto)
                .collect(Collectors.toList());
    }

    private DocumentResponseDTO convertDocumentToDto(Document document) {
        if (document == null) {
            return null;
        }

        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId());
        dto.setOriginalFileName(document.getOriginalFileName());
        dto.setFileSize(document.getFileSize());
        dto.setUploadedAt(document.getUploadedAt());

        if (document.getUploadedByUser() != null) {
            TaskResponseDTO.UserInfoDTO uploaderDto = new TaskResponseDTO.UserInfoDTO();
            uploaderDto.setId(document.getUploadedByUser().getId());
            uploaderDto.setFirstName(document.getUploadedByUser().getFirstName());
            uploaderDto.setLastName(document.getUploadedByUser().getLastName());
            dto.setUploadedByUser(uploaderDto);
        }

        return dto;
    }
}