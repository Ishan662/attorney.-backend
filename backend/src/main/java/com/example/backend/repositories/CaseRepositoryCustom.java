package com.example.backend.repositories;

import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CaseRepositoryCustom {

    List<Case> findCasesWithDynamicFilters(
            AppRole userRole,
            UUID firmOrUserId, // This will be firmId for Lawyers, userId for others
            String searchTerm,
            String caseType,
            String court,
            CaseStatus status,
            Instant startDate,
            Instant endDate
    );
}