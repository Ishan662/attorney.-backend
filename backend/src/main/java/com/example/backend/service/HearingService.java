// >> In a new file: service/HearingService.java
package com.example.backend.service;

import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.mapper.HearingMapper;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.repositories.UserRepository;
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

        Hearing newHearing = hearingMapper.createDtoToEntity(createDto);
        newHearing.setaCase(aCase);
        newHearing.setCreatedByUser(currentUser);
        // Status defaults to PLANNED in the entity

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
}