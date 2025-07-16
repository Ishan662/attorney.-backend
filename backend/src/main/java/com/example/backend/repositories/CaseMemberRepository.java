// >> In a new file: CaseMemberRepository.java
package com.example.backend.repositories;

import com.example.backend.model.cases.CaseMember;
import com.example.backend.model.cases.CaseMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseMemberRepository extends JpaRepository<CaseMember, CaseMemberId> {
    // This repository allows direct interaction with the join table,
    // for example, if you need to find a membership record and delete it
    // to remove a user from a case.
}