package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.mapper.HearingMapper;
import com.example.backend.model.hearing.Hearing;
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

    public ClientDashboardService(HearingRepository hearingRepository, HearingMapper hearingMapper) {
        this.hearingRepository = hearingRepository;
        this.hearingMapper = hearingMapper;
    }

    public List<HearingDTO> getUpcomingHearings(UUID clientId) {
        List<Hearing> hearings = hearingRepository.findUpcomingHearingsByClientId(clientId);
        return hearings.stream()
                .map(hearingMapper::toHearingDto)
                .toList();
    }
}

