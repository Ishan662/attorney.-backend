// >> In a new file: repositories/SubscriptionRepository.java
package com.example.backend.repositories;

import com.example.backend.model.subcription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    // We will add more specific finders here later as needed.
}