package com.example.backend.service;

import com.example.backend.dto.CreateMeetingRequestDto;
import com.example.backend.dto.MeetingRequestDto;
import com.example.backend.mapper.MeetingRequestMapper;
import com.example.backend.model.MeetingRequest;
import com.example.backend.model.MeetingStatus;
import com.example.backend.repositories.MeetingRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingRequestServiceImpl implements MeetingRequestService {

    private final MeetingRequestRepository repository;
    private final MeetingRequestMapper mapper;

    public MeetingRequestServiceImpl(MeetingRequestRepository repository,
                                     MeetingRequestMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public MeetingRequestDto create(CreateMeetingRequestDto dto) {
        // Basic validation: end after start
        if (dto.getEndTime().isBefore(dto.getStartTime()) || dto.getEndTime().equals(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check overlapping meetings
        var overlaps = repository.findByMeetingDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                dto.getMeetingDate(), dto.getEndTime(), dto.getStartTime());

        if (!overlaps.isEmpty()) {
            // You can return a custom error or exception
            throw new IllegalStateException("Requested slot overlaps with existing meeting");
        }

        MeetingRequest entity = mapper.toEntity(dto);
        entity.setStatus(MeetingStatus.PENDING);
        // createdBy already set in dto optionally
        var saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public MeetingRequestDto getById(Long id) {
        var opt = repository.findById(id);
        return opt.map(mapper::toDto).orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
    }

    @Override
    public List<MeetingRequestDto> getByDate(LocalDate date) {
        return repository.findByMeetingDate(date).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MeetingRequestDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public MeetingRequestDto updateStatus(Long id, String status) {
        var entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Meeting not found"));
        try {
            entity.setStatus(MeetingStatus.valueOf(status));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid status");
        }
        var saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

