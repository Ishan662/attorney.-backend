package com.example.backend.mapper;

import com.example.backend.dto.CreateMeetingRequestDto;
import com.example.backend.dto.MeetingRequestDto;
import com.example.backend.model.MeetingRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MeetingRequestMapper {

    private final ModelMapper modelMapper;

    public MeetingRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MeetingRequest toEntity(CreateMeetingRequestDto dto) {
        return modelMapper.map(dto, MeetingRequest.class);
    }

    public MeetingRequestDto toDto(MeetingRequest entity) {
        return modelMapper.map(entity, MeetingRequestDto.class);
    }
}
