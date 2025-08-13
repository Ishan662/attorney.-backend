// >> In your existing file: controllers/LawyerController.java
package com.example.backend.controllers;

import com.example.backend.dto.lawyerDTOS.CourtColorsRequest;
import com.example.backend.dto.lawyerDTOS.LawyerProfileDTO;
import com.example.backend.model.user.User;
import com.example.backend.repositories.UserRepository; // CRITICAL: Import UserRepository
import com.example.backend.service.LawyerService;
import com.example.backend.service.AuthService; // Still useful for other potential tasks
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
    // --- ▼▼▼ INJECT UserRepository so the helper can use it ▼▼▼ ---
    private final UserRepository userRepository;
    // We don't strictly need AuthService here anymore if the helper is local, but it's good practice to keep it for future use.
    private final AuthService authService;

    @Autowired
    public LawyerController(LawyerService lawyerService, AuthService authService, UserRepository userRepository) {
        this.lawyerService = lawyerService;
        this.authService = authService;
        this.userRepository = userRepository; // Initialize the injected repository
    }
    // --- ▲▲▲ INJECT UserRepository so the helper can use it ▲▲▲ ---


    @GetMapping("/profile/me") // Using a more descriptive path
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<LawyerProfileDTO> getMyProfile() {
        // Now we can call our local, fixed helper method
        User currentUser = getCurrentUser();
        UUID currentUserId = currentUser.getId();

        LawyerProfileDTO lawyerProfile = lawyerService.getLawyerProfileByUserId(currentUserId);
        return ResponseEntity.ok(lawyerProfile);
    }

    @PutMapping("/court-colors") // Use PUT for updating resources
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<LawyerProfileDTO> updateMyCourtColors(@RequestBody CourtColorsRequest courtColorsRequest) {
        // Reuse the helper method
        User currentUser = getCurrentUser();
        UUID currentUserId = currentUser.getId();

        Map<String, String> newColors = courtColorsRequest.getCourtColors();

        LawyerProfileDTO updatedProfile = lawyerService.updateCourtColors(currentUserId, newColors);
        return ResponseEntity.ok(updatedProfile);
    }


    // --- ▼▼▼ THIS IS THE CORRECTED HELPER METHOD ▼▼▼ ---
    /**
     * A private helper method within the controller to get the
     * currently authenticated user entity from the database.
     * @return The authenticated User object.
     */
    private User getCurrentUser() {
        // 1. Get the firebase UID from the security context.
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Use the INJECTED userRepository (this.userRepository) to find the user.
        //    Do NOT create a new 'UserRepository userRepository = null;' here.
        return this.userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database. This should not happen if the token filter is working correctly."));
    }
    // --- ▲▲▲ THIS IS THE CORRECTED HELPER METHOD ▲▲▲ ---
}