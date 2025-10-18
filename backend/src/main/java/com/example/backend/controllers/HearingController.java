package com.example.backend.controllers;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.hearingDTOS.UpdateHearingDto;
import com.example.backend.service.HearingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hearings")
public class HearingController {

    private final HearingService hearingService;

    public HearingController(HearingService hearingService) {
        this.hearingService = hearingService;
    }

    // Get all hearings for a specific case
    @GetMapping("/by-case/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HearingDTO>> getHearingsForCase(@PathVariable("caseId") UUID caseId) {
        List<HearingDTO> hearings = hearingService.getHearingsForCase(caseId);
        return ResponseEntity.ok(hearings);
    }

    // Create a hearing for a specific case
    @PostMapping("/for-case/{caseId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<HearingDTO> createHearing(@PathVariable("caseId") UUID caseId,
                                                    @RequestBody CreateHearingDto createDto) {
        HearingDTO newHearing = hearingService.createHearingForCase(caseId, createDto);
        return new ResponseEntity<>(newHearing, HttpStatus.CREATED);
    }

    // Update a hearing
    @PutMapping("/{hearingId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<HearingDTO> updateHearing(@PathVariable("hearingId") UUID hearingId,
                                                    @RequestBody UpdateHearingDto updateDto) {
        HearingDTO updatedHearing = hearingService.updateHearing(hearingId, updateDto);
        return ResponseEntity.ok(updatedHearing);
    }

    // Delete a hearing
    @DeleteMapping("/{hearingId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<Void> deleteHearing(@PathVariable("hearingId") UUID hearingId) {
        hearingService.deleteHearing(hearingId);
        return ResponseEntity.noContent().build();
    }

    // Get all hearings for the currently authenticated user
    @GetMapping("/my-hearings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HearingDTO>> getMyHearings() {
        List<HearingDTO> hearings = hearingService.getHearingsByCurrentUser();
        return ResponseEntity.ok(hearings);
    }
}
