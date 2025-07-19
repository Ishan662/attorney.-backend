package com.example.backend.mapper;

// Import the new response DTO
import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.dto.caseDTOS.UpdateCaseRequest;
import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.PaymentStatus;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.hearing.HearingStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

@Component
public class CaseMapper {

    /**
     * Converts a Case database entity into a CaseResponseDTO for sending to the client.
     * This is the single responsibility of this mapper.
     *
     * @param entity The Case entity from the database.
     * @return A CaseResponseDTO object, or null if the entity was null.
     */
    public CaseResponseDTO toResponseDto(Case entity) {
        if (entity == null) {
            return null;
        }

        CaseResponseDTO dto = new CaseResponseDTO();

        // --- Map all the fields from the updated Case entity to the new DTO ---

        // Core Identifiers
        dto.setId(entity.getId());
        dto.setCaseTitle(entity.getCaseTitle());
        dto.setCaseNumber(entity.getCaseNumber());
        dto.setCaseType(entity.getCaseType());

        // Parties
        dto.setClientName(entity.getClientName());
        dto.setClientPhone(entity.getClientPhone());
        dto.setClientEmail(entity.getClientEmail());
        dto.setOpposingPartyName(entity.getOpposingPartyName());

        // Details
        dto.setCourtName(entity.getCourtName());
        dto.setDescription(entity.getDescription());

        // Status & Financials
        dto.setStatus(entity.getStatus());
        dto.setAgreedFee(entity.getAgreedFee());
        dto.setPaymentStatus(entity.getPaymentStatus());
//        dto.setPaymentStatus(PaymentStatus.valueOf(entity.getPaymentStatus().toString().replace('_', ' ')));


        // 1. Map the Case Owner's Name
        if (entity.getCreatedBy() != null) {
            String ownerName = (entity.getCreatedBy().getFirstName() + " " + entity.getCreatedBy().getLastName()).trim();
            dto.setOwner(ownerName);
        }

        // 2. Find and Map the Next Upcoming Hearing Date
        Optional<Hearing> nextHearing = entity.getHearings().stream()
                .filter(h -> h.getStatus() == HearingStatus.PLANNED && h.getHearingDate().isAfter(Instant.now()))
                .min(Comparator.comparing(Hearing::getHearingDate));

        // If a next hearing is found, set its date in the DTO
        nextHearing.ifPresent(hearing -> dto.setNextHearing(hearing.getHearingDate()));

        // 3. Find and Map the Name of ONE Assigned Junior
        Optional<String> juniorName = entity.getMembers().stream()
                .map(member -> (member.getUser().getFirstName() + " " + member.getUser().getLastName()).trim())
                .findFirst(); // We just grab the first junior we find for the list view

        // If a junior is found, set their name in the DTO
        juniorName.ifPresent(dto::setJunior);


        // Auditing Timestamps
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // get the firm name and lawyer name
        if (entity.getFirm() != null) {
            dto.setFirmName(entity.getFirm().getFirmName());
        }

        Optional<String> ownerName = entity.getMembers().stream()
                .filter(member -> member.getUser().getRole() == AppRole.LAWYER)
                .map(member -> (member.getUser().getFirstName() + " " + member.getUser().getLastName()).trim())
                .findFirst(); // Assumes there's one primary lawyer per case membership

        // If a lawyer is found among the members, set their name.
        ownerName.ifPresent(dto::setOwnerLawyerName);


        return dto;
    }


    // --- ▼▼▼ ADD THIS NEW METHOD ▼▼▼ ---
    /**
     * Updates an existing Case entity from an UpdateCaseRequest DTO.
     * It only maps the fields present in the DTO, ensuring that immutable fields
     * like id, firmId, and createdAt are not touched.
     *
     * @param dto The DTO containing the new data.
     * @param entity The existing entity from the database to be updated.
     */
    public void updateCaseFromDto(UpdateCaseRequest dto, Case entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Use the DTO's getters and the entity's setters to apply changes.
        // We can add checks to only update if the new value is not null.
        if (dto.getCaseTitle() != null) entity.setCaseTitle(dto.getCaseTitle());
        if (dto.getCaseNumber() != null) entity.setCaseNumber(dto.getCaseNumber());
        if (dto.getCaseType() != null) entity.setCaseType(dto.getCaseType());
        if (dto.getCourtName() != null) entity.setCourtName(dto.getCourtName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getClientName() != null) entity.setClientName(dto.getClientName());
        if (dto.getClientPhone() != null) entity.setClientPhone(dto.getClientPhone());
        if (dto.getClientEmail() != null) entity.setClientEmail(dto.getClientEmail());
        if (dto.getOpposingPartyName() != null) entity.setOpposingPartyName(dto.getOpposingPartyName());
        if (dto.getAgreedFee() != null) entity.setAgreedFee(dto.getAgreedFee());
        if (dto.getPaymentStatus() != null) entity.setPaymentStatus(dto.getPaymentStatus());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
    }
    // --- ▲▲▲ ADD THIS NEW METHOD ▲▲▲ ---
}