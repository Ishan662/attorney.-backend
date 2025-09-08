// >> In a new file: service/HearingService.java
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

import java.util.List;
import java.util.UUID;

@Service
public class HearingService {

    @Autowired private HearingRepository hearingRepository;
    @Autowired private CaseRepository caseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HearingMapper hearingMapper;
    @Autowired
    private CalenderValidationService calenderValidationService;

    public List<HearingDTO> getHearingsForCase(UUID caseId) {
        User currentUser = getCurrentUser();
        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);
        List<Hearing> hearings = hearingRepository.findByaCase_IdOrderByHearingDateAsc(aCase.getId());
        return hearingMapper.toHearingDtoList(hearings);
    }

    @Transactional
    public HearingDTO createHearingForCase(UUID caseId, CreateHearingDto createDto) {
        User currentUser = getCurrentUser();
        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);

        UUID firmId = aCase.getFirm().getId();
        User lawyer = userRepository.findFirstByFirmIdAndRole(firmId, AppRole.LAWYER)
                .orElseThrow(() -> new RuntimeException("No lawyer found for this firm"));

        // Map DTO → Entity
        Hearing newHearing = hearingMapper.createDtoToEntity(createDto);
        newHearing.setLawyer(lawyer);
        newHearing.setaCase(aCase);
        newHearing.setCreatedByUser(currentUser);

        // Fetch existing hearings for this lawyer on the same day
        List<Hearing> existing = hearingRepository
                .findByLawyerIdAndDate(lawyer.getId(), newHearing.getStartTime().toLocalDate());

        // Validate travel & overlap
        ValidationResult result = calenderValidationService.validateNewHearing(newHearing, existing);
        if (!result.isValid()) {
            throw new HearingValidationException(result.getMessage());
        }

        // Save only after validation passes
        Hearing savedHearing = hearingRepository.save(newHearing);
        return hearingMapper.toHearingDto(savedHearing);
    }


    // Private helper to get current user and verify their access to a case
    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Case findCaseAndVerifyAccess(UUID caseId, User user) {
        Case aCase = caseRepository.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
        // A user has access if the case belongs to their firm. More granular checks can be added.
        if (!aCase.getFirm().getId().equals(user.getFirm().getId())) {
            throw new AccessDeniedException("You do not have permission to access this case.");
        }
        return aCase;
    }

    @Transactional
    public HearingDTO updateHearing(UUID hearingId, UpdateHearingDto updateDto) {
        User currentUser = getCurrentUser();
        Hearing hearing = hearingRepository.findById(hearingId)
                .orElseThrow(() -> new RuntimeException("Hearing not found"));

        // Verify the user has access to the case this hearing belongs to
        findCaseAndVerifyAccess(hearing.getaCase().getId(), currentUser);

        // Update the hearing entity with the new data from the DTO
        hearing.setTitle(updateDto.getTitle());
        hearing.setHearingDate(updateDto.getHearingDate());
        hearing.setLocation(updateDto.getLocation());
        hearing.setNote(updateDto.getNote());
        hearing.setStatus(updateDto.getStatus());

        Hearing updatedHearing = hearingRepository.save(hearing);
        return hearingMapper.toHearingDto(updatedHearing);
    }

    @Transactional
    public void deleteHearing(UUID hearingId) {
        User currentUser = getCurrentUser();
        Hearing hearing = hearingRepository.findById(hearingId)
                .orElseThrow(() -> new RuntimeException("Hearing not found"));

        // Verify the user has access before allowing deletion
        findCaseAndVerifyAccess(hearing.getaCase().getId(), currentUser);

        hearingRepository.delete(hearing);
    }
}