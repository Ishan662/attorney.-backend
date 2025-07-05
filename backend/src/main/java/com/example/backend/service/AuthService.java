package com.example.backend.service;

import com.example.backend.dto.UserDTO;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.Firm;
import com.example.backend.model.User;
import com.example.backend.model.UserStatus; // Import the new enum
import com.example.backend.repositories.FirmRepository;
import com.example.backend.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FirmRepository firmRepository;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(UserRepository userRepository, FirmRepository firmRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.firmRepository = firmRepository;
        this.userMapper = userMapper;
    }

    /**
     * Handles the registration of a brand-new lawyer.
     * Contains the critical security check to prevent invited users from using this flow.
     */
    @Transactional
    public UserDTO registerNewLawyer(String firebaseTokenString, Map<String, String> profileData) {
        FirebaseToken decodedToken = verifyFirebaseToken(firebaseTokenString);
        String email = decodedToken.getEmail();

        // If a user record with this email already exists (even a pending one),
        // it means they were invited. Block this registration attempt.
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("This email address is already registered or has a pending invitation. Please log in or use your invitation link.");
        }

        Firm newFirm = new Firm();
        String fullNameFromToken = decodedToken.getName();
        String firmNameBasis = (profileData != null && profileData.get("firstName") != null)
                ? profileData.get("firstName") + " " + profileData.get("lastName")
                : fullNameFromToken;
        newFirm.setFirmName(firmNameBasis != null ? firmNameBasis.trim() + "'s Law Firm" : "New Law Firm");
        firmRepository.save(newFirm);

        // --- Create the User ---
        User newUser = new User();
        newUser.setFirebaseUid(decodedToken.getUid());
        newUser.setEmail(email);
        newUser.setRole(AppRole.LAWYER);
        newUser.setFirm(newFirm);
        newUser.setStatus(UserStatus.ACTIVE);
        parseAndSetUserName(newUser, fullNameFromToken, profileData);

        User savedUser = userRepository.save(newUser);

        // TODO: Create a new TRIAL subscription for the new firm here.

        return userMapper.toUserDTO(savedUser);
    }

    /**
     * Fetches the profile for the currently authenticated user.
     * This is used for all returning users upon login to hydrate the frontend.
     */
    public UserDTO getSessionInfoForCurrentUser() {
        // Get the firebase_uid from the security context which was populated by our filter
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in database."));

        // TODO: Here you can build a richer DTO with subscription details and permissions.
        return userMapper.toUserDTO(currentUser);
    }

    private FirebaseToken verifyFirebaseToken(String tokenString) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(tokenString);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid or expired Firebase Token", e);
        }
    }

    // Updated this helper to accept the profileData map
    private void parseAndSetUserName(User user, String fullName, Map<String, String> profileData) {
        if (profileData != null && profileData.get("firstName") != null) {
            user.setFirstName(profileData.get("firstName"));
            user.setLastName(profileData.get("lastName"));
        } else if (fullName != null && !fullName.trim().isEmpty()) {
            String[] nameParts = fullName.split("\\s+");
            user.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                user.setLastName(String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length)));
            }
        }
    }
}