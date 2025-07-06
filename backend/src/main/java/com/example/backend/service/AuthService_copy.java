//package com.example.backend.service;
//
//import com.example.backend.dto.UserDTO;
//import com.example.backend.mapper.UserMapper;
//import com.example.backend.model.AppRole;
//import com.example.backend.model.firm.Firm;
//import com.example.backend.model.user.User;
//import com.example.backend.repositories.FirmRepository;
//import com.example.backend.repositories.UserRepository;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseToken;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.Map;
//
//@Service
//public class AuthService_copy {
//
//    private final UserRepository userRepository;
//    private final FirmRepository firmRepository;
//    private final UserMapper userMapper; // Inject the mapper
//
//    @Autowired
//    public AuthService_copy(UserRepository userRepository, FirmRepository firmRepository, UserMapper userMapper) {
//        this.userRepository = userRepository;
//        this.firmRepository = firmRepository;
//        this.userMapper = userMapper;
//    }
//
//    /**
//     * Processes user login/registration and returns a safe Data Transfer Object (DTO).
//     * @return UserDTO containing data safe to expose to the client.
//     */
//    @Transactional
//    public UserDTO processUserLogin(String firebaseTokenString, Map<String, String> profileData) {
//        try {
//            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseTokenString);
//            String uid = decodedToken.getUid();
//            String email = decodedToken.getEmail();
//            String fullNameFromToken = decodedToken.getName();
//
//            // This variable will hold the final User entity to be mapped
//            User finalUserEntity;
//
//            var existingUserByUid = userRepository.findByFirebaseUid(uid);
//            if (existingUserByUid.isPresent()) {
//                // Scenario 1: Returning user
//                finalUserEntity = existingUserByUid.get();
//            } else {
//                var invitedUser = userRepository.findByEmail(email);
//                if (invitedUser.isPresent() && invitedUser.get().getFirebaseUid() == null) {
//                    // Scenario 2: Invited user completing signup
//                    User userToActivate = invitedUser.get();
//                    userToActivate.setFirebaseUid(uid);
//                    if (profileData != null) {
//                        userToActivate.setFirstName(profileData.get("firstName"));
//                        userToActivate.setLastName(profileData.get("lastName"));
//                    } else {
//                        parseAndSetUserName(userToActivate, fullNameFromToken);
//                    }
//                    userToActivate.setActive(true);
//                    finalUserEntity = userRepository.save(userToActivate);
//                } else {
//                    // Scenario 3: Brand new user
//                    finalUserEntity = createNewLawyerAndFirm(uid, email, fullNameFromToken, profileData);
//                }
//            }
//
//            // Convert the final, persisted User entity into a safe DTO before returning
//            return userMapper.toUserDTO(finalUserEntity);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Authentication processing failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Internal helper method. It works with and returns the raw User entity.
//     * The conversion to DTO happens in the public method.
//     */
//    private User createNewLawyerAndFirm(String uid, String email, String fullNameFromToken, Map<String, String> profileData) {
//        Firm newFirm = new Firm();
//        String firmNameBasis = (profileData != null && profileData.get("firstName") != null)
//                ? profileData.get("firstName") + " " + profileData.get("lastName")
//                : fullNameFromToken;
//        newFirm.setFirmName(firmNameBasis != null ? firmNameBasis.trim() + "'s Law Firm" : "New Law Firm");
//        firmRepository.save(newFirm);
//
//        User newUser = new User();
//        if (profileData != null && profileData.get("firstName") != null) {
//            newUser.setFirstName(profileData.get("firstName"));
//            newUser.setLastName(profileData.get("lastName"));
//            // newUser.setPhoneNumber(profileData.get("phoneNumber")); // Can add this later
//        } else {
//            parseAndSetUserName(newUser, fullNameFromToken);
//        }
//
//        newUser.setFirebaseUid(uid);
//        newUser.setEmail(email);
//        newUser.setRole(AppRole.LAWYER);
//        newUser.setFirm(newFirm);
//        newUser.setActive(true);
//
//        return userRepository.save(newUser);
//    }
//
//    private void parseAndSetUserName(User user, String fullName) {
//        if (fullName != null && !fullName.trim().isEmpty()) {
//            String[] nameParts = fullName.split("\\s+");
//            user.setFirstName(nameParts[0]);
//            if (nameParts.length > 1) {
//                user.setLastName(String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length)));
//            } else {
//                user.setLastName("");
//            }
//        }
//    }
//}