// >> In a NEW file: controllers/TeamController.java
package com.example.backend.controllers;

import com.example.backend.dto.userDTO.UserDTO; // Make sure your UserDTO path is correct
import com.example.backend.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}