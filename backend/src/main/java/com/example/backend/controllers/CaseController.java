package com.example.backend.controllers;

// --- ▼▼▼ IMPORT THE NEW, SPECIFIC DTOS ▼▼▼ ---
import com.example.backend.dto.caseDTOS.CaseDetailDTO;
import com.example.backend.dto.caseDTOS.CreateCaseRequest;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
// --- ▲▲▲ IMPORT THE NEW, SPECIFIC DTOS ▲▲▲ ---

import com.example.backend.dto.caseDTOS.UpdateCaseRequest;
import com.example.backend.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    /**
     * API endpoint to create a new case.
     * Accepts a CreateCaseRequest object tailored to the creation form.
     * Returns the UUID of the newly created case.
     */
    @PostMapping
    @PreAuthorize("hasRole('LAWYER')")
    // --- ▼▼▼ CHANGE 1: UPDATE THE METHOD SIGNATURE ▼▼▼ ---
    public ResponseEntity<UUID> createCase(@RequestBody CreateCaseRequest createCaseRequest) {
        UUID newCaseId = caseService.createCase(createCaseRequest);
        return new ResponseEntity<>(newCaseId, HttpStatus.CREATED);
    }
    // --- ▲▲▲ CHANGE 1: UPDATE THE METHOD SIGNATURE ▲▲▲ ---

    /**
     * API endpoint to get all cases relevant to the currently logged-in user.
     * Returns a list of CaseResponseDTOs, which are safe for client consumption.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    // --- ▼▼▼ CHANGE 2: UPDATE THE RESPONSE TYPE ▼▼▼ ---
    public ResponseEntity<List<CaseResponseDTO>> getMyCases() {
        List<CaseResponseDTO> cases = caseService.getCasesForCurrentUser();
        return ResponseEntity.ok(cases);
    }
    // --- ▲▲▲ CHANGE 2: UPDATE THE RESPONSE TYPE ▲▲▲ ---

    /**
     * API endpoint to get a single, specific case by its ID.
     * Returns a single CaseResponseDTO.
     */
    @GetMapping("/{caseId}")
    @PreAuthorize("isAuthenticated()")
    // --- ▼▼▼ CHANGE 3: UPDATE THE RESPONSE TYPE ▼▼▼ ---
    public ResponseEntity<CaseDetailDTO> getCaseById(@PathVariable UUID caseId) {
        CaseDetailDTO caseResponseDTO = caseService.getCaseById(caseId);
        return ResponseEntity.ok(caseResponseDTO);
    }
    // --- ▲▲▲ CHANGE 3: UPDATE THE RESPONSE TYPE ▲▲▲ ---

//    /**
//     * API endpoint to update an existing case.
//     * This should be updated to use a specific UpdateCaseRequest DTO in a future step.
//     */
//    @PutMapping("/{caseId}")
//    @PreAuthorize("hasRole('LAWYER')")
//    // --- ▼▼▼ CHANGE 4: UPDATE THE RESPONSE TYPE (and acknowledge the input type should also change) ▼▼▼ ---
//    public ResponseEntity<CaseResponseDTO> updateCase(@PathVariable UUID caseId, @RequestBody /* UpdateCaseRequest updateRequest */ Object requestBody) {
//        // CaseResponseDTO updatedCase = caseService.updateCase(caseId, updateRequest);
//        // return ResponseEntity.ok(updatedCase);
//        // For now, we'll leave this unimplemented until we create the UpdateCaseRequest DTO.
//        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
//    }
//    // --- ▲▲▲ CHANGE 4: UPDATE THE RESPONSE TYPE ▲▲▲ ---

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
    // --- ▼▼▼ UPDATE THIS METHOD ▼▼▼ ---
    public ResponseEntity<CaseResponseDTO> updateCase(
            @PathVariable UUID caseId,
            @RequestBody UpdateCaseRequest updateRequest) {

        CaseResponseDTO updatedCase = caseService.updateCase(caseId, updateRequest);
        return ResponseEntity.ok(updatedCase);
    }

}