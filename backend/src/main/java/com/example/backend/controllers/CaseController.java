package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.CaseDTO;
import com.example.backend.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases") // All endpoints in this controller will start with /api/cases
public class CaseController {

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    /**
     * API endpoint to create a new case.
     * Security: Only accessible by users with the 'LAWYER' role.
     *
     * @param caseDTO The data for the new case from the request body.
     * @return The created case data with a 201 CREATED status.
     **/
    @PostMapping
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<CaseDTO> createCase(@RequestBody CaseDTO caseDTO) {
        CaseDTO createdCase = caseService.createCase(caseDTO);
        return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
    }

    /**
     * API endpoint to get all cases relevant to the currently logged-in user.
     * Security: Any authenticated user can call this. The service layer is responsible
     * for filtering the results based on the user's role (Lawyer sees all in firm,
     * Junior/Client sees only their assigned cases).
     *
     * @return A list of cases the user is allowed to see.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CaseDTO>> getMyCases() {
        List<CaseDTO> cases = caseService.getCasesForCurrentUser();
        return ResponseEntity.ok(cases);
    }

    /**
     * API endpoint to get a single, specific case by its ID.
     * Security: Any authenticated user can attempt this, but the service layer
     * will perform a strict check to ensure they are either the firm owner or a member of that specific case.
     *
     * @param caseId The UUID of the case to retrieve.
     * @return The specific case data.
     */
    @GetMapping("/{caseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CaseDTO> getCaseById(@PathVariable UUID caseId) {
        CaseDTO caseDTO = caseService.getCaseById(caseId);
        return ResponseEntity.ok(caseDTO);
    }

    /**
     * API endpoint to update an existing case.
     * Security: Only accessible by users with the 'LAWYER' role. The service layer
     * will further verify that the lawyer owns the case they are trying to update.
     *
     * @param caseId  The ID of the case to update.
     * @param caseDTO The new data for the case.
     * @return The updated case data.
     */
    @PutMapping("/{caseId}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<CaseDTO> updateCase(@PathVariable UUID caseId, @RequestBody CaseDTO caseDTO) {
        CaseDTO updatedCase = caseService.updateCase(caseId, caseDTO);
        return ResponseEntity.ok(updatedCase);
    }

    /**
     * API endpoint to "soft-delete" (archive) a case.
     * Security: Only accessible by users with the 'LAWYER' role. The service layer
     * will verify ownership before archiving.
     *
     * @param caseId The ID of the case to archive.
     * @return An empty response with a 204 No Content status.
     */
    @DeleteMapping("/{caseId}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<Void> archiveCase(@PathVariable UUID caseId) {
        caseService.archiveCase(caseId);
        return ResponseEntity.noContent().build();
    }
}