package com.example.backend.service;

import com.example.backend.dto.caseDTOS.CaseDTO;
import com.example.backend.mapper.CaseMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;

    @Autowired
    public CaseService(CaseRepository caseRepository, UserRepository userRepository, CaseMapper caseMapper) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
    }

    @Transactional
    public CaseDTO createCase(CaseDTO caseDTO) {
        User currentUser = getCurrentUser();

        // A Lawyer can only create a case for their own firm.
        if (currentUser.getRole() != AppRole.LAWYER) {
            throw new AccessDeniedException("Only lawyers can create new cases.");
        }

        Case newCase = new Case();
        // Use the mapper to transfer all user-provided data from the DTO to the new entity.
        caseMapper.updateFromDto(caseDTO, newCase);

        // Set system-managed fields that the user cannot provide.
        newCase.setFirm(currentUser.getFirm());
        newCase.setCreatedAt(Instant.now());
        newCase.setUpdatedAt(Instant.now());
        // If you were to add back createdBy, you would set it here:
        // newCase.setCreatedBy(currentUser);

        Case savedCase = caseRepository.save(newCase);

        // Convert the final, saved entity back to a DTO to return to the client.
        return caseMapper.toDto(savedCase);
    }

    public List<CaseDTO> getCasesForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Case> cases;

        if (currentUser.getRole() == AppRole.LAWYER) {
            // A lawyer sees all non-archived cases in their firm.
            cases = caseRepository.findAllByFirmId(currentUser.getFirm().getId())
                    .stream()
                    .filter(c -> c.getStatus() != CaseStatus.ARCHIVED)
                    .collect(Collectors.toList());
        } else {
            // A Junior or Client only sees the non-archived cases they are an explicit member of.
            cases = caseRepository.findCasesByMemberUserId(currentUser.getId())
                    .stream()
                    .filter(c -> c.getStatus() != CaseStatus.ARCHIVED)
                    .collect(Collectors.toList());
        }

        // Use the mapper to convert the list of entities to a list of DTOs.
        return cases.stream()
                .map(caseMapper::toDto)
                .collect(Collectors.toList());
    }

    public CaseDTO getCaseById(UUID caseId) {
        User currentUser = getCurrentUser();
        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // SECURITY CHECK: Ensure the user has access before returning data.
        if (currentUser.getRole() == AppRole.LAWYER) {
            if (!aCase.getFirm().getId().equals(currentUser.getFirm().getId())) {
                throw new AccessDeniedException("You do not have permission to view this case.");
            }
        } else { // For Junior or Client
            boolean isMember = aCase.getMembers().stream()
                    .anyMatch(member -> member.getUser().getId().equals(currentUser.getId()));
            if (!isMember) {
                throw new AccessDeniedException("You do not have permission to view this case.");
            }
        }

        return caseMapper.toDto(aCase);
    }

    @Transactional
    public CaseDTO updateCase(UUID caseId, CaseDTO caseDTO) {
        User currentUser = getCurrentUser();
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // SECURITY CHECK: A lawyer can only update a case that belongs to their own firm.
        if (!existingCase.getFirm().getId().equals(currentUser.getFirm().getId())) {
            throw new AccessDeniedException("You do not have permission to update this case.");
        }

        caseMapper.updateFromDto(caseDTO, existingCase);
        existingCase.setUpdatedAt(Instant.now());

        Case updatedCase = caseRepository.save(existingCase);
        return caseMapper.toDto(updatedCase);
    }

    @Transactional
    public void archiveCase(UUID caseId) {
        User currentUser = getCurrentUser();
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // SECURITY CHECK: A lawyer can only archive a case that belongs to their own firm.
        if (!existingCase.getFirm().getId().equals(currentUser.getFirm().getId())) {
            throw new AccessDeniedException("You do not have permission to archive this case.");
        }

        existingCase.setStatus(CaseStatus.ARCHIVED);
        existingCase.setUpdatedAt(Instant.now());
        caseRepository.save(existingCase);
    }

    // A private helper method to get the current, fully-hydrated User object from the database.
    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database. This should not happen."));
    }
}