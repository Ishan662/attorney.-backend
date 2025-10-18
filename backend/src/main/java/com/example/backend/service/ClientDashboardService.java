package com.example.backend.service;

import com.example.backend.dto.caseDTOS.CaseResponseDTO;
import com.example.backend.model.cases.Case;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.repositories.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service providing data for the client dashboard, focusing on upcoming hearings.
 */
@Service
public class ClientDashboardService {

    private final CaseRepository caseRepository;

    @Autowired
    public ClientDashboardService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * Finds all cases for a user that have a future hearing and populates the DTO.
     *
     * @param userId The UUID of the user.
     * @return A list of cases sorted by the next hearing date.
     */
    public List<CaseResponseDTO> getUpcomingCases(UUID userId) {
        // 1. Get all cases associated with the user from the database.
        List<Case> userCases = caseRepository.findCasesByUserId(userId);

        // 2. Process the list of cases.
        return userCases.stream()
                .flatMap(aCase -> {
                    // For each case, find its soonest hearing in the future.
                    Optional<Hearing> nextHearingOpt = findNextHearing(aCase);

                    // If a future hearing was found...
                    if (nextHearingOpt.isPresent()) {
                        // ...create a response object, set the hearing date, and include it.
                        CaseResponseDTO dto = mapCaseToResponseDTO(aCase);
                        dto.setNextHearing(nextHearingOpt.get().getHearingDate());
                        return Stream.of(dto);
                    }

                    // If no future hearing exists, filter out this case by returning an empty stream.
                    return Stream.empty();
                })
                // 3. Sort the final list of cases by the hearing date.
                .sorted(Comparator.comparing(CaseResponseDTO::getNextHearing))
                // 4. Collect the results into a list.
                .collect(Collectors.toList());
    }

    /**
     * Helper method to find the earliest hearing in the future from a case's list of hearings.
     */
    private Optional<Hearing> findNextHearing(Case aCase) {
        return aCase.getHearings().stream()
                // Keep only hearings that are in the future.
                .filter(hearing -> hearing.getHearingDate() != null && hearing.getHearingDate().isAfter(Instant.now()))
                // Find the one with the earliest date.
                .min(Comparator.comparing(Hearing::getHearingDate));
    }

    /**
     * Helper method to manually create a CaseResponseDTO from a Case entity.
     * This uses only the getter methods available in the provided Case.java file.
     */
    private CaseResponseDTO mapCaseToResponseDTO(Case aCase) {
        CaseResponseDTO dto = new CaseResponseDTO();

        dto.setId(aCase.getId());
        dto.setCaseTitle(aCase.getCaseTitle());
        dto.setCaseType(aCase.getCaseType());
        dto.setCaseNumber(aCase.getCaseNumber());
        dto.setDescription(aCase.getDescription());
        dto.setCourtName(aCase.getCourtName());
        dto.setCourtType(aCase.getCourtType());
        dto.setClientName(aCase.getClientName());
        dto.setClientPhone(aCase.getClientPhone());
        dto.setClientEmail(aCase.getClientEmail());
        dto.setOpposingPartyName(aCase.getOpposingPartyName());
        dto.setStatus(aCase.getStatus());
        dto.setPaymentStatus(aCase.getPaymentStatus());
        dto.setAgreedFee(aCase.getAgreedFee());
        dto.setCreatedAt(aCase.getCreatedAt());
        dto.setUpdatedAt(aCase.getUpdatedAt());

        // Fields like 'owner', 'firmName', etc., are left null as populating them
        // would require changing other files or assuming methods exist.

        return dto;
    }
}