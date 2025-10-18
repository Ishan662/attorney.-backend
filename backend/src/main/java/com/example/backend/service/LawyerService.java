package com.example.backend.service;

import com.example.backend.dto.lawyerDTOS.LawyerProfileDTO;
import com.example.backend.mapper.LawyerMapper;
import com.example.backend.model.lawyer.Lawyer;
import com.example.backend.model.user.User;
import com.example.backend.repositories.LawyerRepository;
import com.example.backend.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public LawyerProfileDTO getLawyerProfileByUserId(UUID userId) {
        Lawyer lawyer = lawyerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Lawyer profile not found for user ID: " + userId));
        return lawyerMapper.toLawyerProfileDTO(lawyer);
    }

    @Transactional
    public LawyerProfileDTO updateOrCreateCourtColors(UUID userId, Map<String, String> newColors) {
        // fetch the User object first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // try to find existing lawyer
        Lawyer lawyer = lawyerRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    // if not found, create a new Lawyer and link the user
                    Lawyer newLawyer = new Lawyer();
                    newLawyer.setUser(user);
                    return newLawyer;
                });

        // update court colors
        lawyer.setCourtColors(newColors);

        // save either new or updated lawyer
        Lawyer savedLawyer = lawyerRepository.save(lawyer);

        return lawyerMapper.toLawyerProfileDTO(savedLawyer);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getCourtColors(UUID userId) {
        Lawyer lawyer = lawyerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Lawyer not found"));
        return lawyer.getCourtColors();
    }
}
