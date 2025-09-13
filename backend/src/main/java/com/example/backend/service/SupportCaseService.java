package com.example.backend.service;

import com.example.backend.dto.support.*;
import com.example.backend.mapper.SupportCaseMapper;
import com.example.backend.model.AppRole;
import com.example.backend.model.support.*;
import com.example.backend.model.user.User;
import com.example.backend.repositories.SupportCaseRepository;
import com.example.backend.repositories.SupportMessageRepository; // You will need to create this
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class SupportCaseService {

    private final SupportCaseRepository supportCaseRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final SupportCaseMapper supportCaseMapper;
    private final AuthService authService; // To get the current user
    private final UserRepository userRepository;

    @Autowired
    public SupportCaseService(SupportCaseRepository supportCaseRepository,
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

    @Transactional
    public SupportCaseDetailDTO createSupportCase(CreateSupportCaseRequest request) {
        User currentUser = getCurrentUser();

        // Create the parent case
        SupportCase supportCase = new SupportCase();
        supportCase.setCreatedByUser(currentUser);
        supportCase.setSubject(request.getSubject());
        supportCase.setPublicId(generatePublicId()); // Helper to create "T24-001"
        supportCase.setStatus(SupportCaseStatus.OPEN); // New cases are OPEN for admin review
        SupportCase savedCase = supportCaseRepository.save(supportCase);

        // Create the first message
        SupportMessage firstMessage = new SupportMessage();
        firstMessage.setSupportCase(savedCase);
        firstMessage.setSentByUser(currentUser);
        firstMessage.setMessageBody(request.getFirstMessage());
        SupportMessage savedMessage = supportMessageRepository.save(firstMessage);

        // TODO: Trigger an email notification to the admin team here.

        return supportCaseMapper.toDetailDto(savedCase, List.of(savedMessage));
    }

    public List<SupportCaseListDTO> findMySupportCases() {
        User currentUser = getCurrentUser();
        List<SupportCase> myCases = supportCaseRepository.findByCreatedByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return supportCaseMapper.toListDtoList(myCases);
    }

    public SupportCaseDetailDTO getSupportCaseById(UUID caseId) {
        User currentUser = getCurrentUser();
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found"));

        // ** CRITICAL SECURITY CHECK **: Ensure the user owns this case.
        if (!supportCase.getCreatedByUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to view this support case.");
        }

        List<SupportMessage> messages = supportMessageRepository.findBySupportCaseIdOrderByCreatedAtAsc(caseId);
        return supportCaseMapper.toDetailDto(supportCase, messages);
    }

    @Transactional
    public SupportMessageDTO addReplyToCase(UUID caseId, CreateSupportMessageRequest request) {
        User currentUser = getCurrentUser();
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found"));

        // ** CRITICAL SECURITY CHECK **
        if (!supportCase.getCreatedByUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to reply to this support case.");
        }

        // Create the new message
        SupportMessage reply = new SupportMessage();
        reply.setSupportCase(supportCase);
        reply.setSentByUser(currentUser);
        reply.setMessageBody(request.getMessageBody());
        SupportMessage savedReply = supportMessageRepository.save(reply);

        // Update the parent case status to show it needs admin attention
        supportCase.setStatus(SupportCaseStatus.PENDING_ADMIN_REPLY);
        supportCaseRepository.save(supportCase);

        // TODO: Notify admin of the new reply.

        return supportCaseMapper.toMessageDto(savedReply);
    }

    @Transactional
    public void closeSupportCase(UUID caseId) {
        User currentUser = getCurrentUser();
        SupportCase supportCase = supportCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Support Case not found"));
        if (!supportCase.getCreatedByUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to close this case.");
        }
        supportCase.setStatus(SupportCaseStatus.CLOSED);
        supportCaseRepository.save(supportCase);
    }

    private String generatePublicId() {
        // In a real system, you would use a database sequence for the numeric part
        // to guarantee uniqueness under high load. This is a good starting point.
        long timestamp = System.currentTimeMillis();
        return "T" + DateTimeFormatter.ofPattern("yyMM").format(java.time.ZonedDateTime.now()) + "-" + (timestamp % 10000);
    }

    // get curent user
    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database."));
    }
}