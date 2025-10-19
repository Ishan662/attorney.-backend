package com.example.backend.service;

import com.example.backend.dto.CreateMeetingRequestDto;
import com.example.backend.dto.MeetingRequestDto;

import java.time.LocalDate;
import java.util.List;

public interface MeetingRequestService {

    MeetingRequestDto create(CreateMeetingRequestDto dto);

    MeetingRequestDto getById(Long id);

    List<MeetingRequestDto> getByDate(LocalDate date);

    List<MeetingRequestDto> getAll();

    MeetingRequestDto updateStatus(Long id, String status);

    void delete(Long id);
}
