package com.example.backend.mapper; // Or a new 'mapper' package

import com.example.backend.dto.UserDTO;
import com.example.backend.model.user.User;
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
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());

        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        dto.setFullName((firstName + " " + lastName).trim());

        if (user.getFirm() != null) {
            dto.setFirmId(user.getFirm().getId());
            dto.setFirmName(user.getFirm().getFirmName());
        }

        return dto;
    }
}