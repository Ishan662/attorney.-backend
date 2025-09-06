package com.example.backend.mapper;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.model.hearing.Hearing;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HearingMapper {

    public HearingDTO toHearingDto(Hearing hearing) {
        if (hearing == null) return null;
        HearingDTO dto = new HearingDTO();
        dto.setId(hearing.getId());
        dto.setTitle(hearing.getTitle());
        dto.setHearingDate(hearing.getHearingDate());
        dto.setLocation(hearing.getLocation());
        dto.setNote(hearing.getNote());
        dto.setStatus(hearing.getStatus());
        return dto;
    }

    public List<HearingDTO> toHearingDtoList(List<Hearing> hearings) {
        if (hearings == null || hearings.isEmpty()) {
            return Collections.emptyList();
        }
        return hearings.stream().map(this::toHearingDto).collect(Collectors.toList());
    }

    public Hearing createDtoToEntity(CreateHearingDto dto) {
        if (dto == null) return null;
        Hearing hearing = new Hearing();
        hearing.setTitle(dto.getTitle());
        hearing.setHearingDate(dto.getHearingDate());
        hearing.setLocation(dto.getLocation());
        hearing.setNote(dto.getNote());
        return hearing;
    }
}