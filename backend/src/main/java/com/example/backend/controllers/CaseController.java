package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.CaseDetailDTO;
import com.example.backend.dto.caseDTOS.CreateCaseRequest;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
// --- ▲▲▲ IMPORT THE NEW, SPECIFIC DTOS ▲▲▲ ---

import com.example.backend.dto.caseDTOS.UpdateCaseRequest;
import com.example.backend.dto.chatDTOS.ChatChannelDTO;
import com.example.backend.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;
    //initialized the case controller
    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<UUID> createCase(@RequestBody CreateCaseRequest createCaseRequest) {
        UUID newCaseId = caseService.createCase(createCaseRequest);
        return new ResponseEntity<>(newCaseId, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CaseResponseDTO>> getMyCases() {
        List<CaseResponseDTO> cases = caseService.getCasesForCurrentUser();
        return ResponseEntity.ok(cases);
    }

    /**
     * API endpoint to get a single, specific case by its ID.
     * Returns a single CaseResponseDTO.
     */
    @GetMapping("/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CaseDetailDTO> getCaseById(@PathVariable UUID caseId) {
        CaseDetailDTO caseResponseDTO = caseService.getCaseById(caseId);
        return ResponseEntity.ok(caseResponseDTO);
    }

    /**
     * API endpoint to "soft-delete" (archive) a case. No changes needed here.
     */
    @DeleteMapping("/{caseId}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Void> archiveCase(@PathVariable UUID caseId) {
        caseService.archiveCase(caseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * API endpoint to update an existing case.
     * Security: Only accessible by users with the 'LAWYER' or 'JUNIOR' role. The service layer
     * will further verify that the user is a member of the case.
     */
    @PutMapping("/{caseId}")
    // You might want to allow Juniors to update cases as well
    @PreAuthorize("hasAnyRole('LAWYER', 'JUNIOR')")
    public ResponseEntity<CaseResponseDTO> updateCase(
            @PathVariable UUID caseId,
            @RequestBody UpdateCaseRequest updateRequest) {

        CaseResponseDTO updatedCase = caseService.updateCase(caseId, updateRequest);
        return ResponseEntity.ok(updatedCase);
    }

    /**
     * Fetch cases for the current user, applying dynamic set of filters.
     * All params are optional.
     */
    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CaseResponseDTO>> findMyCases(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String caseType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String court,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        List<CaseResponseDTO> cases = caseService.findCasesForCurrentUser(searchTerm, caseType, status, court, startDate, endDate);
        return ResponseEntity.ok(cases);
    }

    /**
     * Fetches all chat channels that the currently authenticated user is a member of.
     *
     * @return A list of ChatChannelDTO objects.
     */
    @GetMapping("/my-chats")
    @PreAuthorize("isAuthenticated()") // Any authenticated user (Lawyer, Junior, Client) can see their own chats
    public ResponseEntity<List<ChatChannelDTO>> getMyChatChannels() {
        List<ChatChannelDTO> chatChannels = caseService.getChatChannelsForCurrentUser();
        return ResponseEntity.ok(chatChannels);
    }

}