package com.example.backend.controllers;

import com.example.backend.dto.support.SupportCaseSummaryDTO;
import com.example.backend.service.SupportDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/support")
public class SupportDashboardController {

    private final SupportDashboardService supportDashboardService;

    public SupportDashboardController(SupportDashboardService supportDashboardService) {
        this.supportDashboardService = supportDashboardService;
    }

    @GetMapping("/lawyer-requests")
    public ResponseEntity<List<SupportCaseSummaryDTO>> getLawyerSupportRequests() {
        List<SupportCaseSummaryDTO> cases = supportDashboardService.getSupportRequestsByLawyers();
        return ResponseEntity.ok(cases);
    }
}
