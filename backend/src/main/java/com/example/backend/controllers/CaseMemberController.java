package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.AssociateMemberRequestDTO;
import com.example.backend.service.CaseMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/members")
@PreAuthorize("hasRole('LAWYER')")
public class CaseMemberController {

    private final CaseMemberService caseMemberService;

    @Autowired
    public CaseMemberController(CaseMemberService caseMemberService) {
        this.caseMemberService = caseMemberService;
    }

    /**
     * Associates an EXISTING user (like a Junior) to an existing case.
     * This is called by the "Associate Junior" button on the case details page.
     */
    @PostMapping
    public ResponseEntity<Void> associateMember(@PathVariable UUID caseId, @RequestBody AssociateMemberRequestDTO request) {
        caseMemberService.associateMemberToCase(caseId, request.getUserId());
        return ResponseEntity.ok().build();
    }
}