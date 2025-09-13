package com.example.backend.controllers;

import com.example.backend.dto.support.*;
import com.example.backend.service.SupportCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support/cases")
// Protect all endpoints in this controller for authenticated users
@PreAuthorize("isAuthenticated()")
public class SupportCaseController {

    private final SupportCaseService supportCaseService;

    @Autowired
    public SupportCaseController(SupportCaseService supportCaseService) {
        this.supportCaseService = supportCaseService;
    }

    @PostMapping
    public ResponseEntity<SupportCaseDetailDTO> createSupportCase(@RequestBody CreateSupportCaseRequest request) {
        SupportCaseDetailDTO createdCase = supportCaseService.createSupportCase(request);
        return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
    }

    @GetMapping("/my-cases")
    public ResponseEntity<List<SupportCaseListDTO>> getMySupportCases() {
        List<SupportCaseListDTO> myCases = supportCaseService.findMySupportCases();
        return ResponseEntity.ok(myCases);
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<SupportCaseDetailDTO> getSupportCaseById(@PathVariable UUID caseId) {
        SupportCaseDetailDTO detailDto = supportCaseService.getSupportCaseById(caseId);
        return ResponseEntity.ok(detailDto);
    }

    @PostMapping("/{caseId}/messages")
    public ResponseEntity<SupportMessageDTO> addReply(@PathVariable UUID caseId, @RequestBody CreateSupportMessageRequest request) {
        SupportMessageDTO replyDto = supportCaseService.addReplyToCase(caseId, request);
        return ResponseEntity.ok(replyDto);
    }

    @PutMapping("/{caseId}/close")
    public ResponseEntity<Void> closeCase(@PathVariable UUID caseId) {
        supportCaseService.closeSupportCase(caseId);
        return ResponseEntity.noContent().build();
    }
}