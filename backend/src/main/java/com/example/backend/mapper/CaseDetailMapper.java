// >> In a NEW file: mapper/CaseDetailMapper.java
package com.example.backend.mapper;

import com.example.backend.dto.caseDTOS.CaseDetailDTO;
import com.example.backend.dto.hearingDTOS.HearingDTO;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CaseDetailMapper {

    /**
     * Converts a full Case entity into the rich CaseDetailDTO for the single-case view.
     */
    public CaseDetailDTO toDetailDto(Case entity) {
        if (entity == null) {
            return null;
        }

        CaseDetailDTO dto = new CaseDetailDTO();

        // Map all the direct fields
        dto.setCaseNumber(entity.getCaseNumber());
        dto.setCaseTitle(entity.getCaseTitle());
        dto.setCaseType(entity.getCaseType());

        dto.setCourtName(entity.getCourtName());
        dto.setCourtType(entity.getCourtType());

        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setClientName(entity.getClientName());
        dto.setClientPhone(entity.getClientPhone());
        dto.setClientEmail(entity.getClientEmail());

        dto.setOpposingPartyName(entity.getOpposingPartyName());
        dto.setAgreedFee(entity.getAgreedFee());
        dto.setPaymentStatus(entity.getPaymentStatus());

        // Find and set the name of an assigned junior
        Optional<String> juniorName = entity.getMembers().stream()
                .filter(member -> member.getUser().getRole() == AppRole.JUNIOR)
                .map(member -> (member.getUser().getFirstName() + " " + member.getUser().getLastName()).trim())
                .findFirst();
        juniorName.ifPresent(dto::setJunior);

        // Map the list of all hearings, sorted by date
        if (entity.getHearings() != null) {
            List<HearingDTO> hearingDtos = entity.getHearings().stream()
                    .sorted(Comparator.comparing(Hearing::getHearingDate))
                    .map(this::toHearingDto) // Call the private helper
                    .collect(Collectors.toList());
            dto.setHearings(hearingDtos);
        }

        if (entity.getFirm() != null) {
            dto.setFirmName(entity.getFirm().getFirmName());
        }

        // 2. Find and map the Owner Lawyer's Name
        // We search through the case members to find the one with the LAWYER role.
        Optional<String> ownerName = entity.getMembers().stream()
                .filter(member -> member.getUser().getRole() == AppRole.LAWYER)
                .map(member -> (member.getUser().getFirstName() + " " + member.getUser().getLastName()).trim())
                .findFirst(); // Assumes there's one primary lawyer per case membership

        // If a lawyer is found among the members, set their name.
        ownerName.ifPresent(dto::setOwnerLawyerName);

        return dto;
    }

    /**
     * A private helper method to convert a single Hearing entity to its DTO.
     * This logic is contained within this mapper because it's only used here.
     */
    private HearingDTO toHearingDto(Hearing hearing) {
        if (hearing == null) return null;
        HearingDTO dto = new HearingDTO();
        dto.setId(hearing.getId());
        dto.setTitle(hearing.getTitle());
        dto.setHearingDate(hearing.getHearingDate());
        dto.setLocation(hearing.getLocation());
        dto.setNote(hearing.getNote());
        dto.setStatus(hearing.getStatus());
        return dto;
    }
}