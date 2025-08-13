package com.example.backend.mapper;

import com.example.backend.dto.lawyerDTOS.LawyerProfileDTO;
import com.example.backend.model.lawyer.Lawyer;
import org.springframework.stereotype.Component;

@Component
public class LawyerMapper {
    public LawyerProfileDTO toLawyerProfileDTO(Lawyer lawyer) {
    if (lawyer == null) {
        return null;
    }

    LawyerProfileDTO dto= new LawyerProfileDTO();

        dto.setCourtColors(lawyer.getCourtColors());

    // Set the user ID for reference.
    // This will trigger the lazy loading while the database session is still open.
        if (lawyer.getUser() != null) {
        dto.setUserId(lawyer.getUser().getId());
    }

        return dto;
}
}
