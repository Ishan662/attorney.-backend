package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.AssignedCaseDTO;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.service.JuniorDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/junior-lawyer")
public class JuniorDashboardController {

    private final JuniorDashboardService juniorDashboardService;

    public JuniorDashboardController(JuniorDashboardService juniorDashboardService) {
        this.juniorDashboardService = juniorDashboardService;
    }

    @GetMapping("/assigned-cases")
    public ResponseEntity<List<AssignedCaseDTO>> getAssignedCases(@RequestParam UUID userId) {
        List<AssignedCaseDTO> assignedCases = juniorDashboardService.getAssignedCases(userId);
        return ResponseEntity.ok(assignedCases);
    }

    @GetMapping("/assigned-case-hearings")
    public ResponseEntity<List<HearingDTO>> getHearings(@RequestParam UUID userId) {
        List<HearingDTO> hearings = juniorDashboardService.getHearingsForAssignedCases(userId);
        return ResponseEntity.ok(hearings);
    }
}
