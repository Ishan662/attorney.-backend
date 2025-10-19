package com.example.backend.controllers;

import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.MeetingRequestDto;
import com.example.backend.service.ClientDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller to handle requests for a client's dashboard,
 * such as upcoming cases and upcoming meetings.
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
     * Fetch all upcoming cases for a given user.
     */
    @GetMapping("/upcoming-cases/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CaseResponseDTO>> getUpcomingCases(@PathVariable UUID userId) {
        List<CaseResponseDTO> upcomingCases = clientDashboardService.getUpcomingCases(userId);
        return ResponseEntity.ok(upcomingCases);
    }

    /**
     * Fetch all upcoming meeting requests (meetingDate after today).
     */
    @GetMapping("/upcoming-meetings/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MeetingRequestDto>> getUpcomingMeetings(@PathVariable UUID userId) {
        List<MeetingRequestDto> upcomingMeetings = clientDashboardService.getUpcomingMeetings(userId);
        return ResponseEntity.ok(upcomingMeetings);
    }
}
