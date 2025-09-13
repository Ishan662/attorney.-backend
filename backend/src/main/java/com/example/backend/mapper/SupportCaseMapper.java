package com.example.backend.mapper;

import com.example.backend.dto.support.SupportCaseDetailDTO;
import com.example.backend.dto.support.SupportCaseListDTO;
import com.example.backend.dto.support.SupportMessageDTO;
import com.example.backend.model.support.SupportCase;
import com.example.backend.model.support.SupportMessage;
import com.example.backend.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupportCaseMapper {

    // Converts a single SupportMessage Entity to its DTO
    public SupportMessageDTO toMessageDto(SupportMessage message) {
        if (message == null) return null;

        SupportMessageDTO dto = new SupportMessageDTO();
        dto.setId(message.getId());
        dto.setMessageBody(message.getMessageBody());
        dto.setCreatedAt(message.getCreatedAt());

        User sender = message.getSentByUser();
        if (sender != null) {
            dto.setSentByUserId(sender.getId());
            dto.setSentByUserName(sender.getFirstName() + " " + sender.getLastName());
            dto.setSentByUserRole(sender.getRole().name());
        }
        return dto;
    }

    // Converts a single SupportCase Entity to a lightweight List DTO
    public SupportCaseListDTO toListDto(SupportCase supportCase) {
        if (supportCase == null) return null;

        SupportCaseListDTO dto = new SupportCaseListDTO();
        dto.setId(supportCase.getId());
        dto.setPublicId(supportCase.getPublicId());
        dto.setSubject(supportCase.getSubject());
        dto.setStatus(supportCase.getStatus());
        dto.setCreatedAt(supportCase.getCreatedAt());

        User creator = supportCase.getCreatedByUser();
        if (creator != null) {
            dto.setCreatedByUserName(creator.getFirstName() + " " + creator.getLastName());
        }
        return dto;
    }

    // Converts a SupportCase Entity and its messages to the full Detail DTO
    public SupportCaseDetailDTO toDetailDto(SupportCase supportCase, List<SupportMessage> messages) {
        if (supportCase == null) return null;

        // Start by converting the main case info
        SupportCaseDetailDTO dto = new SupportCaseDetailDTO();
        dto.setId(supportCase.getId());
        dto.setPublicId(supportCase.getPublicId());
        dto.setSubject(supportCase.getSubject());
        dto.setStatus(supportCase.getStatus());
        dto.setCreatedAt(supportCase.getCreatedAt());

        User creator = supportCase.getCreatedByUser();
        if (creator != null) {
            dto.setCreatedByUserName(creator.getFirstName() + " " + creator.getLastName());
        }

        // Convert the list of message entities to message DTOs
        List<SupportMessageDTO> messageDtos = messages.stream()
                .sorted(Comparator.comparing(SupportMessage::getCreatedAt)) // Ensure messages are in order
                .map(this::toMessageDto)
                .collect(Collectors.toList());

        dto.setMessages(messageDtos);

        return dto;
    }

    // Converts a list of SupportCase entities to a list of List DTOs
    public List<SupportCaseListDTO> toListDtoList(List<SupportCase> cases) {
        return cases.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }
}