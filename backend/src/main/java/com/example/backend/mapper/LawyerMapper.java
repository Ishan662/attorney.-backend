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

        if (lawyer.getUser() != null) {
        dto.setUserId(lawyer.getUser().getId());
    }

        return dto;
}
}
