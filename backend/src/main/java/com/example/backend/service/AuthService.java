// >> In your existing file: AuthService.java
package com.example.backend.service;

import com.example.backend.dto.userDTO.UserDTO;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.firm.Firm;
import com.example.backend.model.user.User;
import com.example.backend.model.UserStatus;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FirmRepository firmRepository;
    private final UserMapper userMapper;
    private final SubscriptionService subscriptionService;

    @Autowired
    public AuthService(UserRepository userRepository, FirmRepository firmRepository, UserMapper userMapper, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.firmRepository = firmRepository;
        this.userMapper = userMapper;
        this.subscriptionService = subscriptionService;
    }

    @Transactional
    public UserDTO registerNewLawyer(String firebaseTokenString, Map<String, String> profileData) {
        FirebaseToken decodedToken = verifyFirebaseToken(firebaseTokenString);
        String email = decodedToken.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("This email address is already registered or has a pending invitation.");
        }

        // --- ▼▼▼ CHANGE: CALL THE NEW HELPER METHOD ▼▼▼ ---
        // The logic is now delegated to the helper method.
        User newUser = createNewLawyerAndFirm(decodedToken, profileData);
        newUser.setStatus(UserStatus.PENDING_PHONE_VERIFICATION);
        User savedUser = userRepository.save(newUser);

        subscriptionService.createTrialSubscriptionForFirm(savedUser.getFirm());

        return userMapper.toUserDTO(savedUser);
    }

    public UserDTO getSessionInfoForCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in database."));
        return userMapper.toUserDTO(currentUser);
    }

    public Map<String, Object> getLoginStatusForCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();

        // We query for the full user object here because we need both status and phone number.
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for status check."));

        // We build a Map to return as our JSON object.
        Map<String, Object> response = new HashMap<>();
        response.put("status", currentUser.getStatus());
        response.put("phoneNumber", currentUser.getPhoneNumber());
        response.put("fullName", currentUser.getFirstName() + " " + currentUser.getLastName());
        response.put("role", currentUser.getRole());

        return response;
    }

    @Transactional
    public UserDTO activateCurrentUserAccount() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User userToActivate = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        if (userToActivate.getStatus() == UserStatus.PENDING_PHONE_VERIFICATION) {
            userToActivate.setStatus(UserStatus.ACTIVE);
            return userMapper.toUserDTO(userRepository.save(userToActivate));
        }
        return userMapper.toUserDTO(userToActivate);
    }

    @Transactional
    public UserDTO processGoogleLogin(String firebaseTokenString) {
        FirebaseToken decodedToken = verifyFirebaseToken(firebaseTokenString);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        Optional<User> existingUserOpt = userRepository.findByFirebaseUid(uid);

        if (existingUserOpt.isPresent()) {
            return userMapper.toUserDTO(existingUserOpt.get());
        } else {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalStateException("This email is associated with a pending invitation.");
            }

            // --- ▼▼▼ CHANGE: CALL THE NEW HELPER METHOD ▼▼▼ ---
            // The logic is now delegated to the helper method. Pass null for profileData
            // as we will get the name from the token itself.
            User newLawyer = createNewLawyerAndFirm(decodedToken, null);
            newLawyer.setStatus(UserStatus.ACTIVE);
            User savedUser = userRepository.save(newLawyer);

            subscriptionService.createTrialSubscriptionForFirm(savedUser.getFirm());

            // TODO: Create TRIAL subscription

            return userMapper.toUserDTO(savedUser);
        }
    }

    // --- ▼▼▼ NEW HELPER METHOD CREATED FROM YOUR EXISTING LOGIC ▼▼▼ ---
    /**
     * A private helper to encapsulate the logic of creating a new Firm and a new User.
     * This is now reusable for both manual registration and Google sign-up.
     * @param decodedToken The verified token from Firebase.
     * @param profileData Optional map of profile data from a form. Can be null.
     * @return The newly created (but not yet saved) User entity.
     */
    private User createNewLawyerAndFirm(FirebaseToken decodedToken, Map<String, String> profileData) {
        // --- Create the Firm ---
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
        newUser.setEmail(decodedToken.getEmail());
//        assert profileData != null;
        if (profileData != null) {
            newUser.setPhoneNumber(profileData.get("phoneNumber"));
        }
        newUser.setRole(AppRole.LAWYER);
        newUser.setFirm(newFirm);
        parseAndSetUserName(newUser, fullNameFromToken, profileData); // Use existing helper to set name

        return newUser;
    }
    // --- ▲▲▲ NEW HELPER METHOD CREATED ▲▲▲ ---


    private FirebaseToken verifyFirebaseToken(String tokenString) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(tokenString);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid or expired Firebase Token", e);
        }
    }

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