package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.user.User;
import com.example.backend.repositories.HearingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {


    @Autowired
    private HearingRepository hearingRepository;

    // Method for today's hearings
    public List<HearingDTO> getTodaysHearings(UUID lawyerId) {
        LocalDate today = LocalDate.now();
        List<Hearing> hearings = hearingRepository.findByLawyerIdAndDate(lawyerId, today);

        return hearings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private HearingDTO convertToDTO(Hearing hearing) {
        HearingDTO dto = new HearingDTO();
        dto.setId(hearing.getId());
        dto.setTitle(hearing.getTitle());
        dto.setHearingDate(hearing.getHearingDate());
        dto.setStartTime(hearing.getStartTime());
        dto.setEndTime(hearing.getEndTime());
        dto.setLocation(hearing.getLocation());
        dto.setNote(hearing.getNote());
        dto.setStatus(hearing.getStatus());
        return dto;
    }

//    public User getUserInfo() {
//        // Implement logic to fetch user info
//        return new User(); // Replace with actual implementation
//    }
//
//    public List<DashboardStats> getStats() {
//        // Implement logic to fetch dashboard statistics
//        return List.of(); // Replace with actual implementation
//    }
//
//    public List<Hearing> getTodaysHearings() {
//        // Implement logic to fetch today's hearings
//        return List.of(); // Replace with actual implementation
//    }
//
//    public List<Meeting> getMeetings() {
//        // Implement logic to fetch meetings
//        return List.of(); // Replace with actual implementation
//    }
//
//    public Map<String, String> getMonthlyIncome() {
//        // Implement logic to fetch monthly income
//        return Map.of(); // Replace with actual implementation
//    }
}