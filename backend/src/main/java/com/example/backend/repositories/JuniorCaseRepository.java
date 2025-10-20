package com.example.backend.repositories;

import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.cases.CaseMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JuniorCaseRepository extends JpaRepository<CaseMember, CaseMemberId> {

    // find all cases where a specific junior lawyer (user) is assigned
    @Query("SELECT cm FROM CaseMember cm WHERE cm.user.id = :juniorLawyerId")
    List<CaseMember> findCasesByJuniorLawyerId(UUID juniorLawyerId);
}
