package com.example.backend.service;

// DTOs
import com.example.backend.dto.caseDTOS.CaseDetailDTO;
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.caseDTOS.CreateCaseRequest;

// Mapper and Model classes
import com.example.backend.dto.caseDTOS.UpdateCaseRequest;
import com.example.backend.dto.chatDTOS.ChatChannelDTO;
import com.example.backend.dto.chatDTOS.MemberDTO;
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
import com.example.backend.service.FirebaseChat.FirebaseChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.time.ZoneOffset;
import java.time.Instant;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final HearingRepository hearingRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final CaseDetailMapper caseDetailMapper;
    private final FirebaseChatService firebaseChatService;

    @Autowired
    public CaseService(CaseRepository caseRepository, UserRepository userRepository, CaseMapper caseMapper,
                       HearingRepository hearingRepository, CaseMemberRepository caseMemberRepository, CaseDetailMapper caseDetailMapper, FirebaseChatService firebaseChatService) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMapper = caseMapper;
        this.hearingRepository = hearingRepository;
        this.caseMemberRepository = caseMemberRepository;
        this.caseDetailMapper = caseDetailMapper;
        this.firebaseChatService = firebaseChatService;
    }

    @Transactional
    public UUID createCase(CreateCaseRequest request) {
        // print incoming request details
        System.out.println("Court Type:" + request.getCourtType());

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



        // Get the case number from the request and update that to a upper case without white spaces.
        String normalizedCaseNumber = null;
        if (request.getCaseNumber() != null && !request.getCaseNumber().trim().isEmpty()) {
            normalizedCaseNumber = request.getCaseNumber().toUpperCase().trim();
        } else {
            throw new IllegalArgumentException("Case Number is required and cannot be empty.");
        }

        // b. Proactively check for duplicates in the same firm.
        if (caseRepository.existsByFirmIdAndCaseNumber(lawyer.getFirm().getId(), normalizedCaseNumber)) {
            throw new IllegalStateException("A case with this number already exists in your firm.");
        }

        newCase.setCourtName(request.getCourt());
        newCase.setCourtType(request.getCourtType());

        newCase.setCaseNumber(normalizedCaseNumber);
        newCase.setCaseType(request.getCaseType());
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
            initialHearing.setLawyer(lawyer);
            hearingRepository.save(initialHearing);
        }

        // ==========================================================
        //  START: NEW FIREBASE CHAT INTEGRATION LOGIC
        // ==========================================================

        List<User> chatMembers = new ArrayList<>();
        chatMembers.add(lawyer); // Add the lawyer creating the case

        // 4. IMPORTANT: NO client user is created here. The invitation flow is now separate.

        // 5. If a junior was associated, find them and add them as a case member.
        if (request.getAssociatedJuniorId() != null) {
            User juniorUser = userRepository.findById(request.getAssociatedJuniorId())
                    .orElseThrow(() -> new IllegalArgumentException("Associated junior not found."));

            if (!juniorUser.getFirm().getId().equals(lawyer.getFirm().getId())) {
                throw new SecurityException("Cannot assign a junior from another firm.");
            }
            caseMemberRepository.save(new CaseMember(savedCase, juniorUser));
            chatMembers.add(juniorUser); // Also add the junior to the chat members list
        }

        // 6. The lawyer who creates the case is automatically a member so they can see it in their "My Cases" view.
        caseMemberRepository.save(new CaseMember(savedCase, lawyer));

        // Only search for the client by email. If they already exist, add them.
        userRepository.findByEmail(request.getClientEmail())
                .ifPresent(chatMembers::add);

        // Create the channel with whomever is currently available
        String channelId = firebaseChatService.createCaseChannel(savedCase.getId(), savedCase.getCaseTitle(), chatMembers);
        if (channelId != null) {
            savedCase.setChatChannelId(channelId);
            caseRepository.save(savedCase);
        }

        return savedCase.getId();
    }

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

    @Transactional
    public CaseResponseDTO updateCase(UUID caseId, UpdateCaseRequest updateRequest) {
        // 1. Get the current user who is making the request.
        User currentUser = getCurrentUser();

        // 2. Fetch the existing case from the database.
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        // This works for both the owning Lawyer and any assigned Juniors.
        boolean isMember = existingCase.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(currentUser.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You do not have permission to update this case.");
        }

        // check the case sensitivity
        if (updateRequest.getCaseNumber() != null) {
            // a. Normalize the incoming new case number.
            String newNormalizedCaseNumber = updateRequest.getCaseNumber().toUpperCase().trim();
            String currentNormalizedCaseNumber = (existingCase.getCaseNumber() != null)
                    ? existingCase.getCaseNumber().toUpperCase().trim()
                    : null;

            if (!newNormalizedCaseNumber.equals(currentNormalizedCaseNumber)) {

                if (caseRepository.existsByFirmIdAndCaseNumber(currentUser.getFirm().getId(), newNormalizedCaseNumber)) {
                    throw new IllegalStateException("A different case with this number already exists in your firm.");
                }
            }
        }

        caseMapper.updateCaseFromDto(updateRequest, existingCase);
        Case updatedCase = caseRepository.save(existingCase);

        return caseMapper.toResponseDto(updatedCase);
    }

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


    /**
     * Fetch cases for the current user, applying dynamic set of filters.
     * All params are optional.
     */
    public List<CaseResponseDTO> findCasesForCurrentUser(
            String searchTerm, String caseType, String status, String court, LocalDate startDate, LocalDate endDate
    ) {
        User currentUser = getCurrentUser();
        List<Case> cases;

        // Convert empty strings from the frontend to nulls for our query
        String finalSearchTerm = (searchTerm != null && !searchTerm.isBlank()) ? searchTerm : null;
        String finalCaseType = (caseType != null && !caseType.isBlank() && !caseType.equals("All Types")) ? caseType : null;
        String finalCourt = (court != null && !court.isBlank() && !court.equals("All Courts")) ? court : null;

        CaseStatus finalStatus = null;
        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("All Cases")) {
            try {
                // This will convert "CLOSED" (String) to CaseStatus.CLOSED (Enum)
                finalStatus = CaseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle cases where the frontend sends an invalid status string
                System.err.println("Invalid status value provided: " + status);
            }
        }

        // The logic is now a simple if/else to call the correct repository method
        if (currentUser.getRole() == AppRole.LAWYER) {
            cases = caseRepository.findCasesForLawyerWithFilters(
                    currentUser.getFirm().getId(),
                    finalSearchTerm,
                    finalCaseType,
                    finalCourt,
                    finalStatus
            );
        } else {
            // JUNIOR or CLIENT
            cases = caseRepository.findCasesForMemberWithFilters(
                    currentUser.getId(),
                    finalSearchTerm,
                    finalCaseType,
                    finalCourt,
                    finalStatus
            );
        }

        // The mapping part remains the same
        return cases.stream()
                .map(caseMapper::toResponseDto)
                .collect(Collectors.toList());
    }


    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }

    public List<ChatChannelDTO> getChatChannelsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Case> cases = caseRepository.findCasesByUserId(currentUser.getId());

        return cases.stream().map(aCase -> {
            ChatChannelDTO channelDTO = new ChatChannelDTO();
            channelDTO.setCaseId(aCase.getId());
            channelDTO.setChatChannelId(aCase.getChatChannelId());
            channelDTO.setCaseTitle(aCase.getCaseTitle());

            List<MemberDTO> memberDTOs = aCase.getMembers().stream().map(member -> {
                MemberDTO memberDTO = new MemberDTO();
                User memberUser = member.getUser();
                memberDTO.setUserId(memberUser.getId());
                memberDTO.setName(memberUser.getFirstName() + " " + memberUser.getLastName());
                memberDTO.setRole(memberUser.getRole());
                return memberDTO;
            }).collect(Collectors.toList());

            channelDTO.setMembers(memberDTOs);
            return channelDTO;
        }).collect(Collectors.toList());
    }
}