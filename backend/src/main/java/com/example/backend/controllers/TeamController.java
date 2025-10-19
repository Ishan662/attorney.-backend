// >> In a NEW file: controllers/TeamController.java
package com.example.backend.controllers;

import com.example.backend.dto.team.*;
import com.example.backend.dto.userDTO.UserDTO; // Make sure your UserDTO path is correct
import com.example.backend.model.AppRole;
import com.example.backend.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/team") // All requests to /api/team will come here
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Endpoint to fetch all JUNIOR users associated with the logged-in LAWYER's firm.
     * This is the endpoint your frontend is trying to call.
     */
    @GetMapping("/juniors")
    // This annotation ensures ONLY a user with the 'LAWYER' role can call this method.
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<List<UserDTO>> getJuniorsForFirm() {
        List<UserDTO> juniors = teamService.getJuniorsForCurrentFirm();
        return ResponseEntity.ok(juniors);
    }

    // --- NEW ENDPOINTS ---

    /**
     * Activates or Deactivates any user (Junior or Client) in the lawyer's firm.
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable UUID userId, @RequestBody UpdateUserStatusRequestDTO request) {
        teamService.updateUserStatus(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Sets or updates the monthly salary for a specific Junior.
     */
    @PutMapping("/juniors/{juniorId}/salary")
    public ResponseEntity<Void> updateJuniorSalary(@PathVariable UUID juniorId, @RequestBody UpdateSalaryRequestDTO request) {
        teamService.updateJuniorSalary(juniorId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Records that a salary payment was made to a Junior.
     */
    @PostMapping("/juniors/{juniorId}/payments")
    public ResponseEntity<Void> recordSalaryPayment(@PathVariable UUID juniorId, @RequestBody RecordSalaryPaymentRequestDTO request) {
        teamService.recordSalaryPayment(juniorId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/juniors-overview")
    public ResponseEntity<List<JuniorLawyerOverviewDTO>> getJuniorsOverview() {
        return ResponseEntity.ok(teamService.getJuniorsOverview());
    }

    @GetMapping("/clients-overview")
    public ResponseEntity<List<ClientOverviewDTO>> getClientsOverview() {
        return ResponseEntity.ok(teamService.getClientsOverview());
    }

    @GetMapping("/users/{userId}/details")
    public ResponseEntity<UserDetailDTO> getUserDetails(@PathVariable UUID userId) {
        return ResponseEntity.ok(teamService.getUserDetails(userId));
    }

    @GetMapping("/juniors/select-list")
    public ResponseEntity<List<TeamMemberSelectDTO>> getJuniorsForSelection() {
        List<TeamMemberSelectDTO> juniors = teamService.getTeamMembersForSelection(AppRole.JUNIOR);
        return ResponseEntity.ok(juniors);
    }

    /**
     * Gets a lightweight list of all CLIENTS in the firm, optimized for populating
     * the "Choose from existing clients" dropdown during case creation.
     */
    @GetMapping("/clients/select-list")
    public ResponseEntity<List<TeamMemberSelectDTO>> getClientsForSelection() {
        List<TeamMemberSelectDTO> clients = teamService.getTeamMembersForSelection(AppRole.CLIENT);
        return ResponseEntity.ok(clients);
    }


}