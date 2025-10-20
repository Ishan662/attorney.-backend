package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.ClosedCasesDTO;
import com.example.backend.model.cases.Case;
import com.example.backend.service.LawyerDaySummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lawyers/day-summary")
public class LawyerDaySummaryController {

    private final LawyerDaySummaryService summaryService;

    public LawyerDaySummaryController(LawyerDaySummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/closed-cases")
    public ResponseEntity<List<ClosedCasesDTO>> getClosedCases(@RequestParam UUID lawyerId) {
        List<ClosedCasesDTO> closedCases = summaryService.getClosedCasesForToday(lawyerId);
        return ResponseEntity.ok(closedCases);
    }

    @GetMapping("/open-cases")
    public ResponseEntity<List<ClosedCasesDTO>> getOpenCases(@RequestParam UUID lawyerId) {
        List<ClosedCasesDTO> openCases = summaryService.getOpenCasesForToday(lawyerId);
        return ResponseEntity.ok(openCases);
    }


}
