// >> In your existing file: controllers/HearingController.java
package com.example.backend.controllers;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.hearingDTOS.UpdateHearingDto; // <-- Import the new DTO
import com.example.backend.service.HearingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

        import java.util.List;
import java.util.UUID;

@RestController
// We move the hearingId to the endpoint level for edit/delete
@RequestMapping("/api/hearings")
public class HearingController {

    @Autowired
    private HearingService hearingService;

    // This endpoint now needs to be on its own path
    @GetMapping("/by-case/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HearingDTO>> getHearingsForCase(@PathVariable UUID caseId) {
        List<HearingDTO> hearings = hearingService.getHearingsForCase(caseId);
        return ResponseEntity.ok(hearings);
    }

    // This endpoint also needs its own path
    @PostMapping("/for-case/{caseId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<HearingDTO> createHearing(@PathVariable UUID caseId, @RequestBody CreateHearingDto createDto) {
        HearingDTO newHearing = hearingService.createHearingForCase(caseId, createDto);
        return new ResponseEntity<>(newHearing, HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing hearing.
     */
    @PutMapping("/{hearingId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<HearingDTO> updateHearing(
            @PathVariable UUID hearingId,
            @RequestBody UpdateHearingDto updateDto) {
        HearingDTO updatedHearing = hearingService.updateHearing(hearingId, updateDto);
        return ResponseEntity.ok(updatedHearing);
    }

    /**
     * Endpoint to delete a hearing.
     */
    @DeleteMapping("/{hearingId}")
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<Void> deleteHearing(@PathVariable UUID hearingId) {
        hearingService.deleteHearing(hearingId);
        return ResponseEntity.noContent().build(); // 204 No Content is standard for successful delete
    }

}
