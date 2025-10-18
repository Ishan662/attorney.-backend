package com.example.backend.controllers;

import com.example.backend.dto.support.*;
import com.example.backend.model.support.SupportCaseStatus;
import com.example.backend.service.AdminSupportCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/support/tickets")
// Protect this entire controller so only ADMINS can access it.
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupportCaseController {

    private final AdminSupportCaseService adminService;

    @Autowired
    public AdminSupportCaseController(AdminSupportCaseService adminService) {
        this.adminService = adminService;
    }

    /**
     * The endpoint for the admin to view the list of tickets.
     * It accepts an optional status filter.
     * e.g., GET /api/admin/support/tickets?status=OPEN
     */
    @GetMapping
    public ResponseEntity<List<SupportCaseListDTO>> getAllSupportCases(
            @RequestParam(required = false) SupportCaseStatus status) {
        List<SupportCaseListDTO> cases = adminService.findAllCases(status);
        return ResponseEntity.ok(cases);
    }

    /**
     * Endpoint for the admin to view a single, detailed ticket.
     */
    @GetMapping("/{caseId}")
    public ResponseEntity<SupportCaseDetailDTO> getSupportCaseDetails(@PathVariable UUID caseId) {
        SupportCaseDetailDTO caseDetails = adminService.getSupportCaseById(caseId);
        return ResponseEntity.ok(caseDetails);
    }

    /**
     * Endpoint for the admin to reply to a ticket.
     * This is the correct mapping for the test file's step 5.
     * The original test file used PUT, but POST is more appropriate for creating a new message.
     */
    @PostMapping("/{caseId}/answer") // Changed from PUT to POST for creating a new message
    public ResponseEntity<SupportMessageDTO> postAdminReply(
            @PathVariable UUID caseId,
            @RequestBody CreateSupportMessageRequest request) {
        SupportMessageDTO reply = adminService.addAdminReply(caseId, request);
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/{caseId}/close")
    public ResponseEntity<Void> closeCaseByAdmin(@PathVariable UUID caseId) {
        adminService.closeCase(caseId);
        return ResponseEntity.noContent().build();
    }
}