package com.example.backend.mapper;

import com.example.backend.dto.CaseDTO;
import com.example.backend.model.cases.Case;
import org.springframework.stereotype.Component;

// @Component registers this class as a Spring Bean, so it can be injected elsewhere.
@Component
public class CaseMapper {

    /**
     * Converts a Case database entity into a CaseDTO for sending to the client.
     * This follows the same null-safe and direct mapping pattern as your UserMapper.
     *
     * @param entity The Case entity from the database.
     * @return A CaseDTO object, or null if the entity was null.
     */
    public CaseDTO toDto(Case entity) {
        // Step 1: Handle the null case, just like in your UserMapper.
        if (entity == null) {
            return null;
        }

        // Step 2: Create a new DTO and map the fields.
        CaseDTO dto = new CaseDTO();

        // Core Case Information
        dto.setId(entity.getId());
        dto.setCaseTitle(entity.getCaseTitle());
        dto.setCaseType(entity.getCaseType());
        dto.setCaseNumber(entity.getCaseNumber());
        dto.setDescription(entity.getDescription());
        dto.setPartyName(entity.getPartyName());
        dto.setCourtName(entity.getCourtName());

        // Status Fields
        dto.setStatus(entity.getStatus());
        dto.setPaymentStatus(entity.getPaymentStatus());

        // Financial Information
        dto.setAgreedFee(entity.getAgreedFee());
        dto.setTotalExpenses(entity.getTotalExpenses());
        dto.setInvoicedAmount(entity.getInvoicedAmount());

        // Auditing/Metadata
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Optional: Flattening related data, just like you did with FirmName.
        // If your Case entity had a 'createdBy' user, you could do this:
        // if (entity.getCreatedBy() != null) {
        //     dto.setCreatedByName(entity.getCreatedBy().getFirstName() + " " + entity.getCreatedBy().getLastName());
        // }

        return dto;
    }

    /**
     * Updates an existing Case entity with data from an incoming CaseDTO.
     * This is used for both creating a new case and updating an existing one.
     *
     * @param dto The CaseDTO received from the client.
     * @param entity The Case entity from the database that needs to be updated.
     */
    public void updateFromDto(CaseDTO dto, Case entity) {
        if (dto == null || entity == null) {
            return;
        }

        // We only update the fields that a user is allowed to change.
        // We intentionally DO NOT set the id, firm, createdAt, or createdBy fields here.
        // The service layer is responsible for setting those managed fields.
        entity.setCaseTitle(dto.getCaseTitle());
        entity.setCaseType(dto.getCaseType());
        entity.setCaseNumber(dto.getCaseNumber());
        entity.setDescription(dto.getDescription());
        entity.setPartyName(dto.getPartyName());
        entity.setCourtName(dto.getCourtName());
        entity.setStatus(dto.getStatus());
        entity.setAgreedFee(dto.getAgreedFee());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setTotalExpenses(dto.getTotalExpenses());
        entity.setInvoicedAmount(dto.getInvoicedAmount());
    }
}