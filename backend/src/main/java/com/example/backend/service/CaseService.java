package com.example.backend.service;

// DTOs
import com.example.backend.dto.caseDTOS.CaseDetailDTO;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.caseDTOS.CreateCaseRequest;

// Mapper and Model classes
import com.example.backend.dto.caseDTOS.UpdateCaseRequest;
import com.example.backend.mapper.CaseDetailMapper;
import com.example.backend.mapper.CaseMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.*;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.hearing.HearingStatus;
import com.example.backend.model.user.User;
// We no longer need UserStatus in this specific method, but it's good to keep
import com.example.backend.model.UserStatus;

// Repositories
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.repositories.CaseMemberRepository;

// Other imports...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final HearingRepository hearingRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CaseDetailMapper caseDetailMapper;

    @Autowired
    public CaseService(CaseRepository caseRepository, UserRepository userRepository, CaseMapper caseMapper,
                       HearingRepository hearingRepository, CaseMemberRepository caseMemberRepository, CaseDetailMapper caseDetailMapper) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
        this.hearingRepository = hearingRepository;
        this.caseMemberRepository = caseMemberRepository;
        this.caseDetailMapper = caseDetailMapper;
    }

    // --- ▼▼▼ THIS IS THE REFACTORED METHOD WITH SIMPLIFIED LOGIC ▼▼▼ ---
    @Transactional
    public UUID createCase(CreateCaseRequest request) {
        // 1. Get the authenticated lawyer creating the case.
        User lawyer = getCurrentUser();
        if (lawyer.getRole() != AppRole.LAWYER) {
            throw new AccessDeniedException("Only lawyers can create new cases.");
        }

        // 2. Create the primary Case entity and populate it directly from the request.
        Case newCase = new Case();
        newCase.setFirm(lawyer.getFirm());
        newCase.setCreatedBy(lawyer);

        // Map all fields directly from the DTO.
        newCase.setClientName(request.getClientName());
        newCase.setClientPhone(request.getClientPhone());
        newCase.setClientEmail(request.getClientEmail());
        newCase.setOpposingPartyName(request.getOpposingPartyName());
        newCase.setCaseNumber(request.getCaseNumber());
        newCase.setCourtName(request.getCourt());
        newCase.setDescription(request.getDescription());
        newCase.setAgreedFee(request.getAgreedFee());
        newCase.setPaymentStatus(request.getPaymentStatus());
        newCase.setCaseTitle(request.getClientName() + " vs " + request.getOpposingPartyName());

        Case savedCase = caseRepository.save(newCase);

        // 3. Create the initial hearing for this case if a date was provided.
        if (request.getInitialHearingDate() != null) {
            Hearing initialHearing = new Hearing();
            initialHearing.setaCase(savedCase);
            initialHearing.setHearingDate(request.getInitialHearingDate().atStartOfDay().toInstant(ZoneOffset.UTC));
            initialHearing.setStatus(HearingStatus.PLANNED);
            initialHearing.setCreatedByUser(lawyer);
            initialHearing.setTitle("Initial Hearing");
            initialHearing.setLocation(request.getCourt());
            hearingRepository.save(initialHearing);
        }

        // 4. IMPORTANT: NO client user is created here. The invitation flow is now separate.
        // The lawyer can invite the client to the portal later, which would then create the User record.

        // 5. If a junior was associated, find them and add them as a case member.
        if (request.getAssociatedJuniorId() != null) {
            User juniorUser = userRepository.findById(request.getAssociatedJuniorId())
                    .orElseThrow(() -> new IllegalArgumentException("Associated junior not found."));

            if (!juniorUser.getFirm().getId().equals(lawyer.getFirm().getId())) {
                throw new SecurityException("Cannot assign a junior from another firm.");
            }
            caseMemberRepository.save(new CaseMember(savedCase, juniorUser));
        }

        // 6. The lawyer who creates the case is automatically a member so they can see it in their "My Cases" view.
        caseMemberRepository.save(new CaseMember(savedCase, lawyer));

        return savedCase.getId();
    }
    // --- ▲▲▲ END OF REFACTORED METHOD ▲▲▲ ---

    // --- OTHER METHODS REMAIN UNCHANGED ---

    public List<CaseResponseDTO> getCasesForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Case> cases;

        if (currentUser.getRole() == AppRole.LAWYER) {
            cases = caseRepository.findAllByFirmId(currentUser.getFirm().getId())
                    .stream()
                    .filter(c -> c.getStatus() != CaseStatus.ARCHIVED)
                    .collect(Collectors.toList());
        } else { // For Junior or Client
            cases = caseRepository.findCasesByMemberUserId(currentUser.getId())
                    .stream()
                    .filter(c -> c.getStatus() != CaseStatus.ARCHIVED)
                    .collect(Collectors.toList());
        }

        return cases.stream()
                .map(caseMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CaseDetailDTO getCaseById(UUID caseId) {
        User currentUser = getCurrentUser();
        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // This security logic is correct and does not need to change.
        boolean hasAccess;
        if (currentUser.getRole() == AppRole.LAWYER) {
            hasAccess = aCase.getFirm().getId().equals(currentUser.getFirm().getId());
        } else { // For Junior or Client
            hasAccess = aCase.getMembers().stream()
                    .anyMatch(member -> member.getUser().getId().equals(currentUser.getId()));
        }

        if (!hasAccess) {
            throw new AccessDeniedException("You do not have permission to view this case.");
        }

        // The only change is here: we now call the new caseDetailMapper
        // to create the rich DTO for the details page.
        return caseDetailMapper.toDetailDto(aCase);
    }

    // --- ▼▼▼ ADD THIS NEW METHOD ▼▼▼ ---
    @Transactional
    public CaseResponseDTO updateCase(UUID caseId, UpdateCaseRequest updateRequest) {
        // 1. Get the current user who is making the request.
        User currentUser = getCurrentUser();

        // 2. Fetch the existing case from the database.
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // 3. Perform a robust security check.
        // A user can only update a case if they are a member of that case.
        // This works for both the owning Lawyer and any assigned Juniors.
        boolean isMember = existingCase.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(currentUser.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You do not have permission to update this case.");
        }

        // 4. Use the mapper to apply the changes from the DTO to the entity.
        caseMapper.updateCaseFromDto(updateRequest, existingCase);

        // The @PreUpdate annotation on the Case entity will handle setting the updatedAt timestamp automatically.

        // 5. Save the updated entity back to the database.
        Case updatedCase = caseRepository.save(existingCase);

        // 6. Convert the final, saved entity to a response DTO and return it.
        return caseMapper.toResponseDto(updatedCase);
    }
    // --- ▲▲▲ ADD THIS NEW METHOD ▲▲▲ ---

    @Transactional
    public void archiveCase(UUID caseId) {
        User currentUser = getCurrentUser();
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        if (!existingCase.getFirm().getId().equals(currentUser.getFirm().getId())) {
            throw new AccessDeniedException("You do not have permission to archive this case.");
        }

        existingCase.setStatus(CaseStatus.ARCHIVED);
        caseRepository.save(existingCase);
    }

    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }
}