//package com.example.backend.mapper;
//
//import com.example.backend.dto.requestDTOS.RequestDTO;
//import com.example.backend.model.requests.Request;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class RequestMapper {
//
//    public RequestDTO toRequestDto(Request request) {
//        if (request == null) return null;
//        RequestDTO dto = new RequestDTO();
//        dto.setId(request.getId());
//        dto.setCaseId(request.getaCase() != null ? request.getaCase().getId() : null);
//        dto.setCreatedByClientId(request.getCreatedByClient() != null ? request.getCreatedByClient().getId() : null);
//        dto.setTitle(request.getTitle());
//        dto.setLocation(request.getLocation());
//        dto.setNote(request.getNote());
//        dto.setRequestedDate(request.getRequestedDate());
//        dto.setCreatedAt(request.getCreatedAt());
//        dto.setUpdatedAt(request.getUpdatedAt());
//        dto.setStatus(request.getStatus());
//        dto.setRequestedLawyerId(request.getRequestedLawyer() != null ? request.getRequestedLawyer().getId() : null);
//        return dto;
//    }
//
//    public List<RequestDTO> toRequestDtoList(List<Request> requests) {
//        if (requests == null || requests.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return requests.stream().map(this::toRequestDto).collect(Collectors.toList());
//    }
//
//    /**
//     * Creates a Request entity from a RequestDTO.
//     * Note: relationships (aCase, createdByClient, requestedLawyer) are NOT resolved here
//     * and should be set by the service layer where repositories are available.
//     */
//    public Request createDtoToEntity(RequestDTO dto) {
//        if (dto == null) return null;
//        Request request = new Request();
//        request.setTitle(dto.getTitle());
//        request.setLocation(dto.getLocation());
//        request.setNote(dto.getNote());
//        request.setRequestedDate(dto.getRequestedDate());
//        // Do not set related entities (Case/User) here; service should resolve them.
//        return request;
//    }
//}

