package com.example.backend.repositories;

import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.cases.CaseMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseMemberRepository extends JpaRepository<CaseMember, CaseMemberId> {
    // This repository allows direct interaction with the join table,
    // for example, if you need to find a membership record and delete it
    // to remove a user from a case.

    // Counts how many cases a specific user is a member of.
    long countByUserId(UUID userId);

    // Finds all case memberships for a specific user.
    List<CaseMember> findByUserId(UUID userId);
}