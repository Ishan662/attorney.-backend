package com.example.backend.service;

import com.example.backend.dto.meeting.ClientMeetingRequestDTO;
import com.example.backend.dto.meeting.LawyerMeetingUpdateDTO;
import com.example.backend.dto.meeting.MeetingResponseDTO;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseMemberId;
import com.example.backend.model.requests.Request;
import com.example.backend.model.requests.RequestStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseMemberRepository;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.RequestRepository;
import com.example.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final CaseMemberRepository caseMemberRepository;

    // A list of statuses that we know are safe to save in the current database schema.
    private static final List<RequestStatus> SUPPORTED_STATUSES = Arrays.asList(
            RequestStatus.PENDING,
            RequestStatus.ACCEPTED,
            RequestStatus.REJECTED, // Assuming REJECTED or DECLINED exists in the DB constraint
            RequestStatus.CANCELLED,
            RequestStatus.COMPLETED
    );

    @Autowired
    public MeetingService(RequestRepository requestRepository, UserRepository userRepository, CaseRepository caseRepository, CaseMemberRepository caseMemberRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.caseRepository = caseRepository;
        this.caseMemberRepository = caseMemberRepository;
    }

    @Transactional
    public MeetingResponseDTO createRequestByClient(ClientMeetingRequestDTO dto) {
        User client = getCurrentUser();
        if (client.getRole() != AppRole.CLIENT) {
            throw new AccessDeniedException("Only clients can create meeting requests.");
        }
        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        Case aCase = caseRepository.findById(dto.getCaseId()).orElseThrow(() -> new EntityNotFoundException("Case not found."));
        if (!caseMemberRepository.existsById(new CaseMemberId(aCase.getId(), client.getId()))) {
            throw new AccessDeniedException("You can only request meetings for cases you are a member of.");
        }
        User lawyer = aCase.getCreatedBy();
        List<Request> overlaps = requestRepository.findOverlappingRequestsForLawyer(lawyer.getId(), dto.getMeetingDate(), dto.getStartTime(), dto.getEndTime());
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("The requested time slot is unavailable.");
        }

        Request newRequest = new Request();
        newRequest.setaCase(aCase);
        newRequest.setCreatedByClient(client);
        newRequest.setRequestedLawyer(lawyer);
        newRequest.setTitle(dto.getTitle());
        newRequest.setMeetingDate(dto.getMeetingDate());
        newRequest.setStartTime(dto.getStartTime());
        newRequest.setEndTime(dto.getEndTime());
        newRequest.setNote(dto.getNote());
        newRequest.setStatus(RequestStatus.PENDING);

        return convertToDto(requestRepository.save(newRequest));
    }

    @Transactional
    public MeetingResponseDTO updateRequestByLawyer(UUID requestId, LawyerMeetingUpdateDTO dto) {
        User lawyer = getCurrentUser();
        if (lawyer.getRole() != AppRole.LAWYER) {
            throw new AccessDeniedException("Only lawyers can manage meeting requests.");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Meeting request not found."));

        if (!request.getRequestedLawyer().getId().equals(lawyer.getId())) {
            throw new AccessDeniedException("You do not have permission to manage this meeting request.");
        }

        RequestStatus newStatus = dto.getNewStatus();

        // --- NEW, SIMPLIFIED AND CORRECT LOGIC ---

        // Set the new status directly. The database now accepts all valid enum values.
        request.setStatus(newStatus);

        // If the lawyer is rescheduling, update the date and time on the *same* request object.
        if (newStatus == RequestStatus.RESCHEDULED) {
            if (dto.getRescheduledDate() == null || dto.getRescheduledStartTime() == null) {
                throw new IllegalArgumentException("Rescheduled date and start time are required for rescheduling.");
            }

            // Before setting the new time, check if it conflicts with other meetings.
            List<Request> overlaps = requestRepository.findOverlappingRequestsForLawyer(
                    lawyer.getId(), dto.getRescheduledDate(), dto.getRescheduledStartTime(), dto.getRescheduledEndTime());

            // Exclude the current request itself from the overlap check.
            overlaps.removeIf(r -> r.getId().equals(requestId));

            if (!overlaps.isEmpty()) {
                throw new IllegalStateException("The proposed reschedule time conflicts with another meeting.");
            }

            request.setMeetingDate(dto.getRescheduledDate());
            request.setStartTime(dto.getRescheduledStartTime());
            request.setEndTime(dto.getRescheduledEndTime());
        }

        // Always update the note if provided.
        if (dto.getNote() != null && !dto.getNote().isBlank()) {
            request.setNote("Lawyer note: " + dto.getNote());
        }

        Request updatedRequest = requestRepository.save(request);
        return convertToDto(updatedRequest);
    }


    @Transactional(readOnly = true)
    public List<MeetingResponseDTO> getMeetingsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Request> requests;
        if (currentUser.getRole() == AppRole.LAWYER) {
            requests = requestRepository.findByRequestedLawyer_Id(currentUser.getId());
        } else if (currentUser.getRole() == AppRole.CLIENT) {
            requests = requestRepository.findByCreatedByClient_Id(currentUser.getId());
        } else {
            return List.of();
        }
        return requests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String firebaseUid = authentication.getName();
        return userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new EntityNotFoundException("Authenticated user not found."));
    }

    private MeetingResponseDTO convertToDto(Request request) {
        MeetingResponseDTO dto = new MeetingResponseDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setMeetingDate(request.getMeetingDate());
        dto.setStartTime(request.getStartTime());
        dto.setEndTime(request.getEndTime());
        dto.setStatus(request.getStatus());
        dto.setNote(request.getNote());
        dto.setCreatedAt(request.getCreatedAt());

        MeetingResponseDTO.UserInfoDTO clientDto = new MeetingResponseDTO.UserInfoDTO();
        clientDto.setId(request.getCreatedByClient().getId());
        clientDto.setFirstName(request.getCreatedByClient().getFirstName());
        clientDto.setLastName(request.getCreatedByClient().getLastName());
        dto.setClient(clientDto);

        MeetingResponseDTO.UserInfoDTO lawyerDto = new MeetingResponseDTO.UserInfoDTO();
        lawyerDto.setId(request.getRequestedLawyer().getId());
        lawyerDto.setFirstName(request.getRequestedLawyer().getFirstName());
        lawyerDto.setLastName(request.getRequestedLawyer().getLastName());
        dto.setLawyer(lawyerDto);

        MeetingResponseDTO.CaseInfoDTO caseDto = new MeetingResponseDTO.CaseInfoDTO();
        caseDto.setId(request.getaCase().getId());
        caseDto.setCaseTitle(request.getaCase().getCaseTitle());
        caseDto.setCaseNumber(request.getaCase().getCaseNumber());
        dto.setaCase(caseDto);

        return dto;
    }
}