// >> In a new file: controllers/TwilioController.java
package com.example.backend.controllers;

import com.example.backend.dto.userDTO.UserDTO;
import com.example.backend.model.user.User;
import com.example.backend.service.AuthService; // We need this to activate the user
import com.example.backend.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp") // A dedicated path for OTP operations
public class TwilioController {

    private final TwilioService twilioService;
    private final AuthService authService;

    @Autowired
    public TwilioController(TwilioService twilioService, AuthService authService) {
        this.twilioService = twilioService;
        this.authService = authService;
    }

    /**
     * Triggers sending an OTP to the currently authenticated user's phone number.
     * The user MUST be logged in (e.g., after email verification) to call this.
     */
    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> sendOtp() {
        // We get the user from our auth service to ensure we have their correct details.
        UserDTO currentUser = authService.getSessionInfoForCurrentUser();
        twilioService.sendVerificationOtp(currentUser.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * Verifies an OTP code and activates the user's account if the code is correct.
     */
    @PostMapping("/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> verifyOtp(@RequestBody Map<String, String> payload) {
        String otpCode = payload.get("otpCode");
        if (otpCode == null || otpCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO currentUser = authService.getSessionInfoForCurrentUser();

        // Ask the Twilio service to check the code.
        boolean isCodeValid = twilioService.checkVerificationOtp(currentUser.getPhoneNumber(), otpCode);

        if (isCodeValid) {
            // If the code is valid, call the AuthService to update the user's status.
            UserDTO activatedUser = authService.activateCurrentUserAccount();
            return ResponseEntity.ok(activatedUser);
        } else {
            // If the code is invalid, return a 400 Bad Request error.
            return ResponseEntity.badRequest().body(null);
        }
    }
}