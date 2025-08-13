package com.example.backend.service;


import com.example.backend.dto.lawyerDTOS.LawyerProfileDTO;
import com.example.backend.mapper.LawyerMapper;
import com.example.backend.model.lawyer.Lawyer;
import com.example.backend.repositories.LawyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class LawyerService {

    @Autowired
    private LawyerRepository lawyerRepository;

    @Autowired
    private LawyerMapper lawyerMapper;

    // The @Transactional annotation is still essential!
    @Transactional(readOnly = true)
    public LawyerProfileDTO getLawyerProfileByUserId(UUID userId) {
        Lawyer lawyer = lawyerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Lawyer profile not found for user ID: " + userId));

        // The call to the mapper happens here, inside the transaction.
        return lawyerMapper.toLawyerProfileDTO(lawyer);
    }

    // Example of how to update the data
    @Transactional
    public LawyerProfileDTO updateCourtColors(UUID userId, Map<String, String> newColors) {
        Lawyer lawyer = lawyerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Lawyer profile not found for user ID: " + userId));

        lawyer.setCourtColors(newColors);
        Lawyer updatedLawyer = lawyerRepository.save(lawyer);

        return lawyerMapper.toLawyerProfileDTO(updatedLawyer);
    }
}