package com.example.backend.service;

import com.example.backend.dto.caseDTOS.CaseDTO;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.mapper.CaseMapper;
import com.example.backend.mapper.HearingMapper;
import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.repositories.JuniorCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JuniorCaseService {

    private final JuniorCaseRepository juniorCaseRepository;
    private final CaseMapper caseMapper;


    public JuniorCaseService(JuniorCaseRepository juniorCaseRepository, CaseMapper caseMapper) {
        this.juniorCaseRepository = juniorCaseRepository;
        this.caseMapper = caseMapper;
    }

    public List<CaseResponseDTO> getCasesAssignedToJunior(UUID juniorLawyerId) {
        List<CaseMember> members = juniorCaseRepository
                .findCasesByJuniorLawyerId(juniorLawyerId);

        return members.stream()
                .map(CaseMember::getaCase)
                .map(caseMapper::toResponseDto)
                .toList();
    }


}
