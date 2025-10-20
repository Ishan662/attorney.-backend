package com.example.backend.service;

import com.example.backend.dto.support.SupportCaseSummaryDTO;
import com.example.backend.model.support.SupportCase;
import com.example.backend.repositories.SupportCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportDashboardService {

    private final SupportCaseRepository supportCaseRepository;

    public SupportDashboardService(SupportCaseRepository supportCaseRepository) {
        this.supportCaseRepository = supportCaseRepository;
    }

    public List<SupportCaseSummaryDTO> getSupportRequestsByLawyers() {
        List<SupportCase> cases = supportCaseRepository.findAllSupportCasesByLawyers();

        return cases.stream().map(sc -> {
            SupportCaseSummaryDTO dto = new SupportCaseSummaryDTO();
            dto.setId(sc.getId());
            dto.setSubject(sc.getSubject());
            dto.setLawyerName(sc.getCreatedByUser().getFirstName() + " " + sc.getCreatedByUser().getLastName());
            dto.setLawyerEmail(sc.getCreatedByUser().getEmail());
            dto.setCreatedAt(sc.getCreatedAt());
            dto.setStatus(sc.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }
}
