package com.example.backend.controllers;

import com.example.backend.model.requests.Request;
import com.example.backend.model.requests.RequestStatus;
import com.example.backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")

public class RequestController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/by-case/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Request>> getRequestsForCase(@PathVariable UUID caseId) {
        List<Request> requests = requestService.getRequestsForCase(caseId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/for-case/{caseId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Request> createRequest(@PathVariable UUID caseId, @RequestBody Request request) {
        Request newRequest = requestService.createRequestForCase(caseId, request);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    @PutMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'CLIENT')")
    public ResponseEntity<Request> updateRequest(@PathVariable UUID requestId, @RequestBody Request request) {
        Request updatedRequest = requestService.updateRequest(requestId, request);
        return ResponseEntity.ok(updatedRequest);
    }

    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'CLIENT')")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestId}/status")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Request> updateRequestStatus(@PathVariable UUID requestId, @RequestParam RequestStatus status) {
        Request updatedRequest = requestService.updateRequestStatus(requestId, status);
        return ResponseEntity.ok(updatedRequest);
    }

}
