package com.example.backend.controllers;


import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.meeting.ClientMeetingRequestDTO;
import com.example.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lawyers/hearings")
public class LawyerDashboardController {

    private final DashboardService dashboardService;

    public LawyerDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    //    @GetMapping("/user/me")
//    public ResponseEntity<User> getUserInfo() {
//        User user = dashboardService.getUserInfo();
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/dashboard/stats")
//    public ResponseEntity<List<DashboardStats>> getDashboardStats() {
//        List<DashboardStats> stats = dashboardService.getStats();
//        return ResponseEntity.ok(stats);
//    }
//
    @GetMapping("/today")
    public ResponseEntity<List<HearingDTO>> getTodaysHearings(@RequestParam UUID lawyerId) {
        return ResponseEntity.ok(dashboardService.getTodaysHearings(lawyerId));
    }

    @GetMapping("/meeting-requests")
    public ResponseEntity<List<ClientMeetingRequestDTO>> getMeetingRequests(@RequestParam UUID lawyerId) {
        List<ClientMeetingRequestDTO> requests = dashboardService.getMeetingRequestsForLawyer(lawyerId);
        return ResponseEntity.ok(requests);
    }


    @GetMapping("/income-chart")
    public ResponseEntity<List<Map<String, Object>>> getIncomeChart(@RequestParam UUID lawyerId) {
        return ResponseEntity.ok(dashboardService.getLawyerIncomeChart(lawyerId));
    }

}