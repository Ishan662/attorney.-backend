package com.example.backend.controllers;

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

}