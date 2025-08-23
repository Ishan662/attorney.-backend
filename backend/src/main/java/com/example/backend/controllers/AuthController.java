package com.example.backend.controllers;

import com.example.backend.dto.userDTO.UserDTO;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register-lawyer")
    public ResponseEntity<UserDTO> registerNewLawyer(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) Map<String, String> profileData
    ){
        String firebaseToken = extractToken(authorizationHeader);
        UserDTO userDto = authService.registerNewLawyer(firebaseToken, profileData);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register-researcher")
    public ResponseEntity<UserDTO> registerNewResearcher(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) Map<String, String> profileData) {
        String firebaseToken = extractToken(authorizationHeader);
        // Call the consistently named service method
        UserDTO userDTO = authService.registerNewResearcher(firebaseToken, profileData);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserSessionInfo(){
        UserDTO userDTO = authService.getSessionInfoForCurrentUser();
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getLoginStatus() {
        Map<String, Object> statusResponse = authService.getLoginStatusForCurrentUser();
        return ResponseEntity.ok(statusResponse);
    }

    @PostMapping("/google-sync")
    public ResponseEntity<UserDTO> syncWithGoogle(@RequestHeader("Authorization") String authorizationHeader) {
        String firebaseToken = extractToken(authorizationHeader);
        UserDTO userDto = authService.processGoogleLogin(firebaseToken);
        return ResponseEntity.ok(userDto);
    }

    // this is for the activating account
    @PostMapping("/activate-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> activateUserAccount() {
        UserDTO activatedUser = authService.activateCurrentUserAccount();
        return ResponseEntity.ok(activatedUser);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // ...THEN it is invalid, so we throw an exception.
            throw new IllegalArgumentException("Authorization header is missing or invalid.");
        }
        return authorizationHeader.substring(7);
    }
}