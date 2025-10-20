package com.example.backend.service;

import com.example.backend.dto.caseDTOS.AssignedCaseDTO;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.repositories.CaseMemberRepository;
import com.example.backend.repositories.JuniorDashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JuniorDashboardService {
    private final JuniorDashboardRepository juniorDashboardRepository;

    public JuniorDashboardService(JuniorDashboardRepository juniorDashboardRepository) {
        this.juniorDashboardRepository = juniorDashboardRepository;
    }

    public List<AssignedCaseDTO> getAssignedCases(UUID juniorLawyerId) {
        List<Case> cases = juniorDashboardRepository.findCasesByUserId(juniorLawyerId);
        return cases.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AssignedCaseDTO toDTO(Case c) {
        AssignedCaseDTO dto = new AssignedCaseDTO();
        dto.setId(c.getId());
        dto.setCaseTitle(c.getCaseTitle());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setClientName(c.getClientName());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }

    public List<HearingDTO> getHearingsForAssignedCases(UUID userId) {
        // Step 1: Get assigned case IDs
        List<Case> assignedCases = juniorDashboardRepository.findCasesByUserId(userId);
        List<UUID> caseIds = assignedCases.stream().map(Case::getId).toList();

        // Step 2: Fetch hearings for those cases
        List<Hearing> hearings = juniorDashboardRepository.findByCaseIds(caseIds);

        // Step 3: Map to DTO
        return hearings.stream().map(this::toHearingDTO).collect(Collectors.toList());
    }

    private HearingDTO toHearingDTO(Hearing h) {
        HearingDTO dto = new HearingDTO();
        dto.setId(h.getId());
        dto.setTitle(h.getTitle());
        dto.setHearingDate(h.getHearingDate());
        dto.setStartTime(h.getStartTime());
        dto.setEndTime(h.getEndTime());
        dto.setLocation(h.getLocation());
        dto.setNote(h.getNote());
        dto.setStatus(h.getStatus());

        // Add case info
        if (h.getaCase() != null) {
            dto.setCaseId(h.getaCase().getId());
            dto.setCaseTitle(h.getaCase().getCaseTitle());
        }

        return dto;
    }
}
