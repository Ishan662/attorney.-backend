package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.service.ClientDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller to handle requests for a client's dashboard, such as upcoming cases.
 */
@RestController
@RequestMapping("/api/client-dashboard")
@CrossOrigin(origins = "*")
public class ClientDashboardController {

    private final ClientDashboardService clientDashboardService;

    @Autowired
    public ClientDashboardController(ClientDashboardService clientDashboardService) {
        this.clientDashboardService = clientDashboardService;
    }

    /**
     * Fetches all cases for a given user that have at least one hearing in the future.
     * The list is sorted by the date of the next upcoming hearing.
     *
     * @param userId The UUID of the user.
     * @return A ResponseEntity containing a list of upcoming cases.
     */
    @GetMapping("/upcoming-cases/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CaseResponseDTO>> getUpcomingCases(@PathVariable UUID userId) {
        List<CaseResponseDTO> upcomingCases = clientDashboardService.getUpcomingCases(userId);
        return ResponseEntity.ok(upcomingCases);
    }
}