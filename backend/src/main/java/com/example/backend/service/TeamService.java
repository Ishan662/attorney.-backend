// >> In a NEW file: service/TeamService.java
package com.example.backend.service;

import com.example.backend.dto.userDTO.UserDTO; // Make sure your UserDTO path is correct
import com.example.backend.mapper.UserMapper;   // Make sure your UserMapper path is correct
import com.example.backend.model.AppRole;
import com.example.backend.model.user.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public TeamService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Fetches all users with the 'JUNIOR' role from the currently
     * authenticated lawyer's firm.
     */
    public List<UserDTO> getJuniorsForCurrentFirm() {
        // 1. Get the currently logged-in user (the lawyer).
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found."));

        // 2. Find all users who share the same firm ID and have the role 'JUNIOR'.
        //    (You must add the 'findByFirmIdAndRole' method to your UserRepository).
        List<User> juniors = userRepository.findByFirmIdAndRole(currentUser.getFirm().getId(), AppRole.JUNIOR);

        // 3. Convert the list of User entities to a list of UserDTOs to send to the frontend.
        return juniors.stream()
                .map(userMapper::toUserDTO)
                .collect(Collectors.toList());
    }
}