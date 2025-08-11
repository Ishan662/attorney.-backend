package com.example.backend.service;


import com.example.backend.dto.lawyerDTOS.CourtColorsRequest;
import com.example.backend.model.lawyer.Lawyer;
import com.example.backend.model.user.User;
import com.example.backend.repositories.LawyerRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LawyerService {

    private final LawyerRepository lawyerRepository;
    private final UserRepository userRepository;

    public LawyerService(LawyerRepository lawyerRepository, UserRepository userRepository) {
        this.lawyerRepository = lawyerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Lawyer saveOrUpdateCourtColors(CourtColorsRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lawyer lawyer = lawyerRepository.findByUserId(request.getUserId())
                .orElse(new Lawyer(user, request.getCourtColors()));

        lawyer.setCourtColors(request.getCourtColors());

        return lawyerRepository.save(lawyer);
    }

}
