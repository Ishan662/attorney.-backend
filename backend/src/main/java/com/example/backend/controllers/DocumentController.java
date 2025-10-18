package com.example.backend.controllers;

import com.example.backend.model.document.Document;
import com.example.backend.service.DocumentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Endpoint for uploading a document AND associating it with a specific task.
     * The task ID is provided in the URL path.
     */
    @PostMapping("/upload/task/{taskId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<?> uploadForTask(
            @PathVariable UUID taskId,
            @RequestParam("file") MultipartFile file) {

        Document document = documentService.uploadDocumentForTask(taskId, file);

        // Return a simple success message with the new document's ID
        return new ResponseEntity<>(Map.of("documentId", document.getId(), "fileName", document.getOriginalFileName()), HttpStatus.CREATED);
    }

    /**
     * Endpoint for securely downloading any document by its ID.
     */
    @GetMapping("/{documentId}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadDocument(@PathVariable UUID documentId, HttpServletRequest request) {
        Resource resource = documentService.getDocumentResource(documentId);

        // Determine file's content type
        String contentType = "application/octet-stream"; // Fallback
        try {
            String detectedType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (detectedType != null) {
                contentType = detectedType;
            }
        } catch (IOException ex) {
            // Log error if needed
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}