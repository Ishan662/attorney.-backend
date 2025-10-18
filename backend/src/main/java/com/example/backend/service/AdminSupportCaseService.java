package com.example.backend.service;

import com.example.backend.dto.support.*;
import com.example.backend.mapper.SupportCaseMapper;
import com.example.backend.model.support.*;
import com.example.backend.model.user.User;
import com.example.backend.repositories.SupportCaseRepository;
import com.example.backend.repositories.SupportMessageRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminSupportCaseService {

    private final SupportCaseRepository supportCaseRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final SupportCaseMapper supportCaseMapper;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public AdminSupportCaseService(SupportCaseRepository supportCaseRepository,
                                   SupportMessageRepository supportMessageRepository,
                                   SupportCaseMapper supportCaseMapper,
                                   AuthService authService,
                                   UserRepository userRepository) {
        this.supportCaseRepository = supportCaseRepository;
        this.supportMessageRepository = supportMessageRepository;
        this.supportCaseMapper = supportCaseMapper;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Finds all support cases, with optional filtering by status.
     * This is for the admin's main dashboard view.
     */
    public List<SupportCaseListDTO> findAllCases(SupportCaseStatus status) {
        Specification<SupportCase> spec = (root, query, cb) -> {
            if (status != null) {
                return cb.equal(root.get("status"), status);
            }
            return cb.conjunction(); // An empty "where" clause if no status is provided
        };
        // Order by creation date to handle the oldest tickets first
        List<SupportCase> cases = supportCaseRepository.findAll(spec,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "createdAt"));
        return supportCaseMapper.toListDtoList(cases);
    }

    /**
     * Gets the full detail of a single ticket, including all messages.
     */
    @Transactional(readOnly = true)
    public SupportCaseDetailDTO getSupportCaseById(UUID caseId) {
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found"));
        List<SupportMessage> messages = supportMessageRepository.findBySupportCaseIdOrderByCreatedAtAsc(caseId);
        return supportCaseMapper.toDetailDto(supportCase, messages);
    }

    /**
     * Allows an admin to post an answer to a ticket.
     */
    @Transactional
    public SupportMessageDTO addAdminReply(UUID caseId, CreateSupportMessageRequest request) {
        User currentAdmin = getCurrentUser();
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found"));

        // Create the new message
        SupportMessage reply = new SupportMessage();
        reply.setSupportCase(supportCase);
        reply.setSentByUser(currentAdmin);
        reply.setMessageBody(request.getMessageBody());
        SupportMessage savedReply = supportMessageRepository.save(reply);

        // Update the parent case status to show it's waiting for the user
        supportCase.setStatus(SupportCaseStatus.PENDING_USER_REPLY);
        supportCaseRepository.save(supportCase);

        // TODO: Trigger email notification to the user who created the ticket.

        return supportCaseMapper.toMessageDto(savedReply);
    }

    @Transactional
    public void closeCase(UUID caseId) {
        // 1. Get the admin user who is performing this action (for auditing purposes, optional).
        // User currentAdmin = authService.getCurrentUser();

        // 2. Find the ticket in the database.
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found with ID: " + caseId));

        // 3. Update the status and set the closed timestamp.
        supportCase.setStatus(SupportCaseStatus.CLOSED);
        supportCase.setClosedAt(Instant.now());

        // 4. Save the changes to the database.
        supportCaseRepository.save(supportCase);

        // Optional TODO: You could add a SupportMessage here automatically
        // like "System: This case was closed by an administrator."
    }

    // get curent user
    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }
}