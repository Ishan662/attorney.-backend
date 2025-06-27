package com.example.backend.controllers;

// Import the DTO, not the Entity
import com.example.backend.dto.UserDTO;
import com.example.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    // The response entity is now parameterized with UserDTO
    public ResponseEntity<UserDTO> processLogin(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) Map<String, String> profileData) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // It's better to throw an exception here that can be handled globally,
            // but for now, this is fine.
            return ResponseEntity.badRequest().build();
        }

        try {
            String firebaseToken = authorizationHeader.substring(7);

            // The service call now returns a DTO
            UserDTO userDto = authService.processUserLogin(firebaseToken, profileData);

            // Return the safe DTO object to the client
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            // In a real app, you would have a @ControllerAdvice to handle exceptions globally
            // For now, re-throwing or returning a generic error is okay.
            // Let's return a more structured error response.
            return ResponseEntity.status(500).build(); // Avoid sending raw exception messages
        }
    }
}