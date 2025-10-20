package com.example.backend.controllers;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.requestDTOS.MeetingRequestDTO;
import com.example.backend.service.ClientDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients/hearings")
public class ClientDashboardController {

    private final ClientDashboardService clientDashboardService;

    public ClientDashboardController(ClientDashboardService clientDashboardService) {
        this.clientDashboardService = clientDashboardService;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<HearingDTO>> getUpcomingHearings(@RequestParam UUID clientId) {
        return ResponseEntity.ok(clientDashboardService.getUpcomingHearings(clientId));
    }

    @GetMapping("/upcoming-meetings")
    public ResponseEntity<List<MeetingRequestDTO>> getUpcomingMeetings(@RequestParam UUID clientId) {
        List<MeetingRequestDTO> upcomingMeetings = clientDashboardService.getUpcomingMeetings(clientId);
        return ResponseEntity.ok(upcomingMeetings);
    }
}