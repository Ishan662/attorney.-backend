// >> In a new file: controllers/HearingController.java
package com.example.backend.controllers;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.service.HearingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases/{caseId}/hearings") // Nested under cases for context
public class HearingController {

    @Autowired
    private HearingService hearingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HearingDTO>> getHearingsForCase(@PathVariable UUID caseId) {
        List<HearingDTO> hearings = hearingService.getHearingsForCase(caseId);
        return ResponseEntity.ok(hearings);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')") // Only lawyers or juniors can add hearings
    public ResponseEntity<HearingDTO> createHearing(
            @PathVariable UUID caseId,
            @RequestBody CreateHearingDto createDto) {
        HearingDTO newHearing = hearingService.createHearingForCase(caseId, createDto);
        return new ResponseEntity<>(newHearing, HttpStatus.CREATED);
    }
}