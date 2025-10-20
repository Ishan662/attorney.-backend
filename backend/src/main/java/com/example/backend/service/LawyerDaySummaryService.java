package com.example.backend.service;

import com.example.backend.dto.caseDTOS.ClosedCasesDTO;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import com.example.backend.repositories.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LawyerDaySummaryService {

    private final CaseRepository caseRepository;

    public LawyerDaySummaryService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<ClosedCasesDTO> getClosedCasesForToday(UUID lawyerId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Case> closedCases = caseRepository.findClosedCasesForLawyerOnDate(
                lawyerId,
                CaseStatus.CLOSED,
                startOfDay,
                endOfDay
        );

        return closedCases.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ClosedCasesDTO> getOpenCasesForToday(UUID lawyerId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<Case> openCases = caseRepository.findOpenedCasesForLawyerOnDate(
                lawyerId,
                CaseStatus.OPEN,
                startOfDay,
                endOfDay
        );

        return openCases.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Mapping method
    private ClosedCasesDTO toDTO(Case c) {
        ClosedCasesDTO dto = new ClosedCasesDTO();
        dto.setId(c.getId());
        dto.setCaseTitle(c.getCaseTitle());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setClientName(c.getClientName());
        dto.setAgreedFee(c.getAgreedFee());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());
        return dto;
    }



}
