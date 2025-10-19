package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.dto.hearingDTOS.UpdateHearingDto;
import com.example.backend.exception.HearingValidationException;
import com.example.backend.mapper.HearingMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.util.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class HearingService {

    @Autowired private HearingRepository hearingRepository;
    @Autowired private CaseRepository caseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HearingMapper hearingMapper;
    @Autowired private CalendarValidationService calendarValidationService;

    public List<HearingDTO> getHearingsForCase(UUID caseId) {
        User currentUser = getCurrentUser();
        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);

        List<Hearing> hearings = hearingRepository.findByaCase_IdOrderByHearingDateAsc(aCase.getId());
        return hearingMapper.toHearingDtoList(hearings);
    }

    public List<HearingDTO> getHearingsByCurrentUser() {
        User currentUser = getCurrentUser();
        List<Hearing> hearings = hearingRepository.findAllByLawyerId(currentUser.getId());
        return hearingMapper.toHearingDtoList(hearings);
    }

    private HearingDTO mapToDto(Hearing hearing) {
        HearingDTO dto = new HearingDTO();
        dto.setId(hearing.getId());
        dto.setTitle(hearing.getTitle());
        dto.setStartTime(hearing.getStartTime());
        dto.setEndTime(hearing.getEndTime());
        dto.setLocation(hearing.getLocation());
        dto.setNote(hearing.getNote());
        dto.setStatus(hearing.getStatus());
        return dto;
    }

    @Transactional
    public HearingDTO createHearingForCase(UUID caseId, CreateHearingDto createDto) {
        User currentUser = getCurrentUser();
        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);

        UUID firmId = aCase.getFirm().getId();
        User lawyer = userRepository
                .findFirstByFirmIdAndRole(firmId, AppRole.LAWYER)
                .orElseThrow(() -> new RuntimeException("No lawyer found for this firm"));

        // map dto → entity
        Hearing newHearing = hearingMapper.createDtoToEntity(createDto);
        newHearing.setLawyer(lawyer);
        newHearing.setaCase(aCase);
        newHearing.setCreatedByUser(currentUser);

        // ✅ if frontend doesn't send startTime or endTime, assign current time
        if (newHearing.getStartTime() == null) {
            newHearing.setStartTime(LocalDateTime.now());
        }
        if (newHearing.getEndTime() == null) {
            newHearing.setEndTime(LocalDateTime.now().plusHours(1)); // default 1 hour later
        }

        // ✅ also make sure hearingDate is set if null
        if (newHearing.getHearingDate() == null) {
            newHearing.setHearingDate(Instant.now());
        }

        // validate travel & overlap using Option 1
        ValidationResult result = calendarValidationService.validateNewHearingWithTasks(
                newHearing.getStartTime(),
                newHearing.getEndTime(),
                newHearing.getLocation(),
                lawyer
        );
        if (!result.isValid()) {
            throw new HearingValidationException(result.getMessage());
        }

        Hearing savedHearing = hearingRepository.save(newHearing);
        return hearingMapper.toHearingDto(savedHearing);
    }

    @Transactional
    public HearingDTO updateHearing(UUID hearingId, UpdateHearingDto updateDto) {
        User currentUser = getCurrentUser();
        Hearing hearing = hearingRepository
                .findById(hearingId)
                .orElseThrow(() -> new RuntimeException("Hearing not found"));

        findCaseAndVerifyAccess(hearing.getaCase().getId(), currentUser);

        hearing.setTitle(updateDto.getTitle());
        hearing.setHearingDate(updateDto.getHearingDate());
        hearing.setStartTime(updateDto.getStartTime());
        hearing.setEndTime(updateDto.getEndTime());
        hearing.setLocation(updateDto.getLocation());
        hearing.setNote(updateDto.getNote());
        hearing.setStatus(updateDto.getStatus());

        // ✅ handle null start/end time in update as well
        if (hearing.getStartTime() == null) {
            hearing.setStartTime(LocalDateTime.now());
        }
        if (hearing.getEndTime() == null) {
            hearing.setEndTime(LocalDateTime.now().plusHours(1));
        }
        if (hearing.getHearingDate() == null) {
            hearing.setHearingDate(Instant.now());
        }

        Hearing updatedHearing = hearingRepository.save(hearing);
        return hearingMapper.toHearingDto(updatedHearing);
    }

    @Transactional
    public void deleteHearing(UUID hearingId) {
        User currentUser = getCurrentUser();
        Hearing hearing = hearingRepository
                .findById(hearingId)
                .orElseThrow(() -> new RuntimeException("Hearing not found"));

        findCaseAndVerifyAccess(hearing.getaCase().getId(), currentUser);
        hearingRepository.delete(hearing);
    }

    // --- helpers ---
    public User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Case findCaseAndVerifyAccess(UUID caseId, User user) {
        Case aCase = caseRepository
                .findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found"));
        if (!aCase.getFirm().getId().equals(user.getFirm().getId())) {
            throw new AccessDeniedException("You do not have permission to access this case.");
        }
        return aCase;
    }
}
