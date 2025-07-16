// >> In a new file: repositories/SubscriptionPlanRepository.java
package com.example.backend.repositories;

import com.example.backend.model.subcription.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Integer> {
    // A method to easily find our trial plan by its unique name.
    Optional<SubscriptionPlan> findByPlanName(String planName);
}