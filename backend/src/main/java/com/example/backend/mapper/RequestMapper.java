package com.example.backend.mapper;

import com.example.backend.dto.meeting.ClientMeetingRequestDTO;
import com.example.backend.model.requests.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public ClientMeetingRequestDTO toDTO(Request request) {
        if (request == null) return null;

        ClientMeetingRequestDTO dto = new ClientMeetingRequestDTO();

        // Set case ID
        if (request.getaCase() != null) {
            dto.setCaseId(request.getaCase().getId());
        }

        // Set basic meeting info
        dto.setTitle(request.getTitle());
        dto.setMeetingDate(request.getMeetingDate());
        dto.setStartTime(request.getStartTime());
        dto.setEndTime(request.getEndTime());
        dto.setNote(request.getNote());

        return dto;
    }
}
