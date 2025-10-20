// >> In a NEW file: src/main/java/com/example/backend/repositories/CaseRepositoryCustomImpl.java

package com.example.backend.repositories;

import com.example.backend.model.AppRole;
import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CaseRepositoryCustomImpl implements CaseRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Case> findCasesWithDynamicFilters(
            AppRole userRole, UUID firmOrUserId, String searchTerm, String caseType,
            String court, CaseStatus status, Instant startDate, Instant endDate) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Case> query = cb.createQuery(Case.class);
        Root<Case> c = query.from(Case.class); // 'c' is our alias for the Case entity

        List<Predicate> predicates = new ArrayList<>();

        // 1. Base Security Predicate (filter by firm or member)
        if (userRole == AppRole.LAWYER) {
            predicates.add(cb.equal(c.get("firm").get("id"), firmOrUserId));
        } else { // JUNIOR or CLIENT
            Join<Object, Object> members = c.join("members");
            predicates.add(cb.equal(members.get("user").get("id"), firmOrUserId));
        }

        // 2. Conditionally add other filters
        if (searchTerm != null) {
            Predicate titleLike = cb.like(cb.lower(c.get("caseTitle")), "%" + searchTerm.toLowerCase() + "%");
            Predicate numberLike = cb.like(cb.lower(c.get("caseNumber")), "%" + searchTerm.toLowerCase() + "%");
            predicates.add(cb.or(titleLike, numberLike));
        }
        if (caseType != null) {
            predicates.add(cb.equal(c.get("caseType"), caseType));
        }
        if (court != null) {
            predicates.add(cb.equal(c.get("courtName"), court));
        }
        if (status != null) {
            predicates.add(cb.equal(c.get("status"), status));
        }

        // 3. Conditionally add date filter
        if (startDate != null || endDate != null) {
            // This requires a subquery to check the hearings collection
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Case> subRoot = subquery.correlate(c);
            Join<Object, Object> hearings = subRoot.join("hearings");
            subquery.select(cb.literal(1L)); // Just check for existence

            List<Predicate> datePredicates = new ArrayList<>();
            if (startDate != null) {
                datePredicates.add(cb.greaterThanOrEqualTo(hearings.get("hearingDate"), startDate));
            }
            if (endDate != null) {
                datePredicates.add(cb.lessThanOrEqualTo(hearings.get("hearingDate"), endDate));
            }
            subquery.where(cb.and(datePredicates.toArray(new Predicate[0])));

            predicates.add(cb.exists(subquery));
        }

        // 4. Combine all predicates and set the final query
        query.where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.desc(c.get("createdAt")))
                .distinct(true);

        // 5. Execute the query
        TypedQuery<Case> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}