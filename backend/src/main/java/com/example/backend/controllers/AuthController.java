package com.example.backend.controllers;

// Import the DTO, not the Entity
import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
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

    @PostMapping("/resigter-lawyer")
    public ResponseEntity<UserDTO> registerNewLawyer(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) Map<String, String> profileData
    ){
        String firebaseToken = extractToken(authorizationHeader);
        UserDTO userDto = authService.registerNewLawyer(firebaseToken, profileData);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserSessionInfo(){
        UserDTO userDTO = authService.getSessionInfoForCurrentUser();
        return ResponseEntity.ok(userDTO);
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is missing or invalid.");
        }
        assert authorizationHeader != null;
        // return the token without the String "Bearer" on it
        return authorizationHeader.substring(7);
    }
}