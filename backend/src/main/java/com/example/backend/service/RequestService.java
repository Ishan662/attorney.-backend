//package com.example.backend.service;
//
//import com.example.backend.model.requests.Request;
//import com.example.backend.model.requests.RequestStatus;
//import com.example.backend.model.cases.Case;
//import com.example.backend.model.user.User;
//import com.example.backend.repositories.RequestRepository;
//import com.example.backend.repositories.CaseRepository;
//import com.example.backend.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//
//public class RequestService {
//    @Autowired private RequestRepository requestRepository;
//    @Autowired private CaseRepository caseRepository;
//    @Autowired private UserRepository userRepository;
//
//    public List<Request> getRequestsForCase(UUID caseId) {
//        User currentUser = getCurrentUser();
//        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);
//        return requestRepository.findByaCase_IdOrderByRequestedDateAsc(aCase.getId());
//    }
//
//    @Transactional
//    public Request createRequestForCase(UUID caseId, Request request) {
//        User currentUser = getCurrentUser();
//        Case aCase = findCaseAndVerifyAccess(caseId, currentUser);
//
//        request.setaCase(aCase);
//        request.setCreatedByClient(currentUser);
//        request.setStatus(RequestStatus.PENDING);
//
//        return requestRepository.save(request);
//    }
//
//    @Transactional
//    public Request updateRequest(UUID requestId, Request updatedRequest) {
//        User currentUser = getCurrentUser();
//        Request request = requestRepository.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Request not found"));
//
//        findCaseAndVerifyAccess(request.getaCase().getId(), currentUser);
//
//        request.setTitle(updatedRequest.getTitle());
//        request.setLocation(updatedRequest.getLocation());
//        request.setNote(updatedRequest.getNote());
//        request.setRequestedDate(updatedRequest.getRequestedDate());
//        request.setRequestedLawyer(updatedRequest.getRequestedLawyer());
//        request.setStatus(updatedRequest.getStatus());
//
//        return requestRepository.save(request);
//    }
//
//    @Transactional
//    public void deleteRequest(UUID requestId) {
//        User currentUser = getCurrentUser();
//        Request request = requestRepository.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Request not found"));
//
//        findCaseAndVerifyAccess(request.getaCase().getId(), currentUser);
//
//        requestRepository.delete(request);
//    }
//
//    @Transactional
//    public Request updateRequestStatus(UUID requestId, RequestStatus status) {
//        User currentUser = getCurrentUser();
//        Request request = requestRepository.findById(requestId)
//                .orElseThrow(() -> new RuntimeException("Request not found"));
//
//        findCaseAndVerifyAccess(request.getaCase().getId(), currentUser);
//
//        request.setStatus(status);
//        return requestRepository.save(request);
//    }
//
//    private User getCurrentUser() {
//        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
//        return userRepository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    private Case findCaseAndVerifyAccess(UUID caseId, User user) {
//        Case aCase = caseRepository.findById(caseId).orElseThrow(() -> new RuntimeException("Case not found"));
//        if (!aCase.getFirm().getId().equals(user.getFirm().getId())) {
//            throw new AccessDeniedException("You do not have permission to access this case.");
//        }
//        return aCase;
//    }
//}
