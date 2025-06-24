package com.example.backend.controllers;

import com.example.backend.model.AppUser;
import com.example.backend.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController

@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody TokenRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());

            String firebaseUid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            Optional<AppUser> existingUser = userRepository.findByFirebaseUid(firebaseUid);

            if (existingUser.isEmpty()) {
                AppUser newUser = new AppUser();
                newUser.setFirebaseUid(firebaseUid);
                newUser.setEmail(email);
                newUser.setVerified(false);
                newUser.setRole("PENDING");
                userRepository.save(newUser);
            }

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    static class TokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}