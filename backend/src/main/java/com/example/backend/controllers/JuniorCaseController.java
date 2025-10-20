package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.CaseDTO;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.service.JuniorCaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/juniors/cases")
public class JuniorCaseController {

    private final JuniorCaseService juniorCaseService;

    public JuniorCaseController(JuniorCaseService juniorCaseService) {
        this.juniorCaseService = juniorCaseService;
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<CaseResponseDTO>> getAssignedCases(@RequestParam UUID juniorLawyerId) {
        return ResponseEntity.ok(juniorCaseService.getCasesAssignedToJunior(juniorLawyerId));
    }
}
