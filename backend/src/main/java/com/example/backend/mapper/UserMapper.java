package com.example.backend.mapper; // Or a new 'mapper' package

import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());

        // This is safe because user.getFullName() is a @Transient method
//        dto.setFullName(user.getFullName());

// Safely access the lazy-loaded Firm object
        if (user.getFirm() != null) {
            dto.setFirmId(user.getFirm().getId());
            dto.setFirmName(user.getFirm().getFirmName());
        }

        return dto;
    }
}