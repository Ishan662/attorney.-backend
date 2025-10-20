// >> In a NEW folder and file: src/main/java/com/example/backend/repositories/specifications/CaseSpecification.java

package com.example.backend.repositories.specifications;

import com.example.backend.model.cases.Case;
import com.example.backend.model.cases.CaseStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class CaseSpecification {

    public static Specification<Case> hasFirmId(UUID firmId) {
        return (root, query, cb) -> cb.equal(root.get("firm").get("id"), firmId);
    }

    public static Specification<Case> hasMemberId(UUID userId) {
        return (root, query, cb) -> {
            // This creates a subquery to check for membership, which is efficient
            // and avoids duplicates that a simple JOIN might cause.
            query.distinct(true);
            return cb.equal(root.join("members").get("user").get("id"), userId);
        };
    }

    public static Specification<Case> containsSearchTerm(String searchTerm) {
        return (root, query, cb) -> {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("caseTitle")), likePattern),
                    cb.like(cb.lower(root.get("caseNumber")), likePattern)
            );
        };
    }

    public static Specification<Case> hasCaseType(String caseType) {
        return (root, query, cb) -> cb.equal(root.get("caseType"), caseType);
    }

    public static Specification<Case> hasCourt(String court) {
        return (root, query, cb) -> cb.equal(root.get("courtName"), court);
    }

    public static Specification<Case> hasStatus(CaseStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    // --- FINAL, CORRECTED DATE FILTER LOGIC ---
    public static Specification<Case> hasHearingBetween(Instant startInstant, Instant endInstant) {
        return (root, query, cb) -> {
            query.distinct(true); // Ensure cases with multiple hearings in range appear once
            if (startInstant != null && endInstant != null) {
                return cb.between(root.join("hearings", JoinType.INNER).get("hearingDate"), startInstant, endInstant);
            } else if (startInstant != null) {
                return cb.greaterThanOrEqualTo(root.join("hearings", JoinType.INNER).get("hearingDate"), startInstant);
            } else if (endInstant != null) {
                return cb.lessThanOrEqualTo(root.join("hearings", JoinType.INNER).get("hearingDate"), endInstant);
            }
            return null; // Should not happen if called correctly
        };
    }
}