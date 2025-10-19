package com.example.backend.service;

import com.example.backend.dto.MeetingRequestDto;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
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
 * including upcoming hearings and upcoming meetings.
 */
@Service
public class ClientDashboardService {

    private final CaseRepository caseRepository;
    private final com.example.backend.service.MeetingRequestServiceImpl meetingRequestService;

    @Autowired
    public ClientDashboardService(CaseRepository caseRepository,
                                  com.example.backend.service.MeetingRequestServiceImpl meetingRequestService) {
        this.caseRepository = caseRepository;
        this.meetingRequestService = meetingRequestService;
    }

    /**
     * Fetch all upcoming hearings (cases) for a given user.
     * A case is considered "upcoming" if it has a hearing date in the future.
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
     * Fetch all upcoming meeting requests for a given user.
     * A meeting is upcoming if its meetingDate is after today.
     */
    public List<MeetingRequestDto> getUpcomingMeetings(UUID userId) {
        List<MeetingRequestDto> allMeetings = meetingRequestService.getAll();
        LocalDate today = LocalDate.now();

        return allMeetings.stream()
                .filter(m -> m.getMeetingDate() != null && m.getMeetingDate().isAfter(today))
                .sorted(Comparator.comparing(MeetingRequestDto::getMeetingDate))
                .collect(Collectors.toList());
    }

    /**
     * Helper to find the next upcoming hearing in a case.
     */
    private Optional<Hearing> findNextHearing(Case aCase) {
        return aCase.getHearings().stream()
                .filter(h -> h.getHearingDate() != null && h.getHearingDate().isAfter(Instant.now()))
                .min(Comparator.comparing(Hearing::getHearingDate));
    }

    /**
     * Helper to convert Case entity to CaseResponseDTO.
     */
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
