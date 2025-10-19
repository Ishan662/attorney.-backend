package com.example.backend.service;

import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.MeetingRequestDto;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.repositories.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service providing data for the client dashboard,
 * including upcoming hearings and meetings.
 */
@Service
public class ClientDashboardService {

    private final CaseRepository caseRepository;
    private final MeetingRequestService meetingRequestService;

    @Autowired
    public ClientDashboardService(CaseRepository caseRepository,
                                  MeetingRequestService meetingRequestService) {
        this.caseRepository = caseRepository;
        this.meetingRequestService = meetingRequestService;
    }

    /**
     * Finds all cases for a user that have a future hearing.
     */
    public List<CaseResponseDTO> getUpcomingCases(UUID userId) {
        List<Case> userCases = caseRepository.findCasesByUserId(userId);

        return userCases.stream()
                .flatMap(aCase -> {
                    Optional<Hearing> nextHearingOpt = findNextHearing(aCase);
                    if (nextHearingOpt.isPresent()) {
                        CaseResponseDTO dto = mapCaseToResponseDTO(aCase);
                        dto.setNextHearing(nextHearingOpt.get().getHearingDate());
                        return Stream.of(dto);
                    }
                    return Stream.empty();
                })
                .sorted(Comparator.comparing(CaseResponseDTO::getNextHearing))
                .collect(Collectors.toList());
    }

    /**
     * Gets upcoming meetings using the existing MeetingRequestService.
     * Filters only meetings with meetingDate after today.
     */
    public List<MeetingRequestDto> getUpcomingMeetings(UUID userId) {
        List<MeetingRequestDto> allMeetings = meetingRequestService.getAll();

        LocalDate today = LocalDate.now();

        return allMeetings.stream()
                .filter(m -> m.getMeetingDate() != null && m.getMeetingDate().isAfter(today))
                .sorted(Comparator.comparing(MeetingRequestDto::getMeetingDate))
                .collect(Collectors.toList());
    }

    private Optional<Hearing> findNextHearing(Case aCase) {
        return aCase.getHearings().stream()
                .filter(hearing -> hearing.getHearingDate() != null && hearing.getHearingDate().isAfter(Instant.now()))
                .min(Comparator.comparing(Hearing::getHearingDate));
    }

    private CaseResponseDTO mapCaseToResponseDTO(Case aCase) {
        CaseResponseDTO dto = new CaseResponseDTO();
        dto.setId(aCase.getId());
        dto.setCaseTitle(aCase.getCaseTitle());
        dto.setCaseType(aCase.getCaseType());
        dto.setCaseNumber(aCase.getCaseNumber());
        dto.setDescription(aCase.getDescription());
        dto.setCourtName(aCase.getCourtName());
        dto.setCourtType(aCase.getCourtType());
        dto.setClientName(aCase.getClientName());
        dto.setClientPhone(aCase.getClientPhone());
        dto.setClientEmail(aCase.getClientEmail());
        dto.setOpposingPartyName(aCase.getOpposingPartyName());
        dto.setStatus(aCase.getStatus());
        dto.setPaymentStatus(aCase.getPaymentStatus());
        dto.setAgreedFee(aCase.getAgreedFee());
        dto.setCreatedAt(aCase.getCreatedAt());
        dto.setUpdatedAt(aCase.getUpdatedAt());
        return dto;
    }
}
