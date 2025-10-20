package com.example.backend.repositories;

import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.hearing.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JuniorDashboardRepository extends JpaRepository<CaseMember, UUID> {
    @Query("SELECT cm.aCase FROM CaseMember cm WHERE cm.user.id = :userId")
    List<Case> findCasesByUserId(@Param("userId") UUID userId);

    @Query("SELECT h FROM Hearing h WHERE h.aCase.id IN :caseIds ORDER BY h.hearingDate ASC")
    List<Hearing> findByCaseIds(@Param("caseIds") List<UUID> caseIds);
}
