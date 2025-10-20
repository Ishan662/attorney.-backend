package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.meeting.ClientMeetingRequestDTO;
import com.example.backend.mapper.RequestMapper;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.user.User;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardService {


    @Autowired
    private HearingRepository hearingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private RequestMapper requestMapper;


    public List<Map<String, Object>> getLawyerIncomeChart(UUID lawyerId) {
        // Step 1: fetch all cases for the lawyer
        // Assuming you have a method to fetch cases by lawyer
        List<UUID> caseIds = getCaseIdsForLawyer(lawyerId);

        // Step 2: fetch payments for each case
        List<Map<String, Object>> chartData = caseIds.stream().map(caseId -> {
            Long totalAmount = paymentRepository.sumSuccessfulPaymentsByCaseId(caseId, PaymentStatus.SUCCESS);

            Map<String, Object> map = new HashMap<>();
            map.put("caseId", caseId);
            map.put("amount", totalAmount);
            return map;
        }).collect(Collectors.toList());

        return chartData;
    }

    private List<UUID> getCaseIdsForLawyer(UUID lawyerId) {
        // Assuming HearingRepository or CaseRepository can give this
        // If CaseRepository exists:
        return hearingRepository.findAllByLawyerId(lawyerId)
                .stream()
                .map(c -> c.getId())
                .collect(Collectors.toList());
    }

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

        if (hearing.getaCase() != null) {
            dto.setCaseId(hearing.getaCase().getId());
            dto.setCaseTitle(hearing.getaCase().getCaseTitle()); // or getCaseTitle(), depending on your Case model
        }

        return dto;
    }

    public List<ClientMeetingRequestDTO> getMeetingRequestsForLawyer(UUID lawyerId) {
        return requestRepository.findAllByRequestedLawyerId(lawyerId)
                .stream()
                .map(requestMapper::toDTO)
                .collect(Collectors.toList());
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