package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.requestDTOS.MeetingRequestDTO;
import com.example.backend.mapper.HearingMapper;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.requests.Request;
import com.example.backend.model.requests.RequestStatus;
import com.example.backend.repositories.ClientDashboardRepository;
import com.example.backend.repositories.HearingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientDashboardService {

    private final HearingRepository hearingRepository;
    private final HearingMapper hearingMapper;
    private final ClientDashboardRepository clientDashboardRepository;


    public ClientDashboardService(HearingRepository hearingRepository, HearingMapper hearingMapper, ClientDashboardRepository clientDashboardRepository) {
        this.hearingRepository = hearingRepository;
        this.hearingMapper = hearingMapper;
        this.clientDashboardRepository = clientDashboardRepository;

    }

    public List<HearingDTO> getUpcomingHearings(UUID clientId) {
        List<Hearing> hearings = hearingRepository.findUpcomingHearingsByClientId(clientId);
        return hearings.stream()
                .map(hearingMapper::toHearingDto)
                .toList();
    }
    public List<MeetingRequestDTO> getUpcomingMeetings(UUID clientId) {
        LocalDate today = LocalDate.now();
        List<Request> requests = clientDashboardRepository.findUpcomingMeetingsForClient(
                clientId, today, RequestStatus.PENDING
        );

        return requests.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private MeetingRequestDTO toDTO(Request r) {
        MeetingRequestDTO dto = new MeetingRequestDTO();
        dto.setId(r.getId());
        dto.setTitle(r.getTitle());
        dto.setLocation(r.getLocation());
        dto.setNote(r.getNote());
        dto.setMeetingDate(r.getMeetingDate());
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        dto.setGoogleMeetLink(r.getGoogleMeetLink());
        dto.setStatus(r.getStatus());
        dto.setCaseId(r.getaCase().getId());
        dto.setCaseTitle(r.getaCase().getCaseTitle());
        return dto;
    }
}

