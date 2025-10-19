package com.example.backend.service;

import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.cases.CaseMemberId;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CaseMemberRepository;
import com.example.backend.repositories.CaseRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.service.FirebaseChat.FirebaseChatService; // Ensure this import is correct
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class CaseMemberService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMemberRepository caseMemberRepository;
    private final FirebaseChatService firebaseChatService;

    @Autowired
    public CaseMemberService(CaseRepository caseRepository, UserRepository userRepository, CaseMemberRepository caseMemberRepository, FirebaseChatService firebaseChatService) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.caseMemberRepository = caseMemberRepository;
        this.firebaseChatService = firebaseChatService;
    }

    @Transactional
    public CaseMember associateMemberToCase(UUID caseId, UUID userId) {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        User lawyer = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with ID: " + caseId));

        User userToAssociate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User to associate not found with ID: " + userId));

        // --- SECURITY CHECKS ---
        if (!aCase.getFirm().getId().equals(lawyer.getFirm().getId()) || !userToAssociate.getFirm().getId().equals(lawyer.getFirm().getId())) {
            throw new SecurityException("Forbidden: You can only manage cases and users within your own firm.");
        }
        if (caseMemberRepository.existsById(new CaseMemberId(caseId, userId))) {
            throw new IllegalStateException("This user is already a member of the case.");
        }

        CaseMember newMember = new CaseMember(aCase, userToAssociate);
        CaseMember savedMember = caseMemberRepository.save(newMember);

        firebaseChatService.addUserToChannel(aCase.getChatChannelId(), userToAssociate);

        return savedMember;
    }
}