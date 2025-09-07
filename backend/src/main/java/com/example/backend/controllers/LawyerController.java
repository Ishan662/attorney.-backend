// >> In your existing file: controllers/LawyerController.java
package com.example.backend.controllers;

import com.example.backend.dto.lawyerDTOS.CourtColorsRequest;
import com.example.backend.dto.lawyerDTOS.LawyerProfileDTO;
import com.example.backend.model.user.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.service.LawyerService;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/lawyers") // Corrected base path to match your request
public class LawyerController {

    private final LawyerService lawyerService;
    private final UserRepository userRepository;

    @Autowired
    public LawyerController(LawyerService lawyerService, AuthService authService, UserRepository userRepository) {
        this.lawyerService = lawyerService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile/me")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<LawyerProfileDTO> getMyProfile() {
        // Now we can call our local, fixed helper method
        User currentUser = getCurrentUser();
        UUID currentUserId = currentUser.getId();

        LawyerProfileDTO lawyerProfile = lawyerService.getLawyerProfileByUserId(currentUserId);
        return ResponseEntity.ok(lawyerProfile);
    }

    @PutMapping("/court-colors") //
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<LawyerProfileDTO> updateMyCourtColors(@RequestBody CourtColorsRequest courtColorsRequest) {

        User currentUser = getCurrentUser();
        UUID currentUserId = currentUser.getId();

        Map<String, String> newColors = courtColorsRequest.getCourtColors();

        LawyerProfileDTO updatedProfile = lawyerService.updateCourtColors(currentUserId, newColors);
        return ResponseEntity.ok(updatedProfile);
    }


    /**
     * A private helper method within the controller to get the
     * currently authenticated user entity from the database.
     * @return The authenticated User object.
     */
    private User getCurrentUser() {

        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();

        return this.userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database. This should not happen if the token filter is working correctly."));
    }
}