// >> In your existing file: service/SubscriptionService.java
package com.example.backend.service;

import com.example.backend.model.subcription.Subscription;
import com.example.backend.model.subcription.SubscriptionPlan;
import com.example.backend.model.subcription.SubscriptionStatus; // Make sure this enum exists
import com.example.backend.model.firm.Firm;
import com.example.backend.repositories.SubscriptionPlanRepository;
import com.example.backend.repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionPlanRepository planRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
    }


    /**
     * Creates a default 14-day trial subscription for a newly created firm.
     * This method is called by the AuthService after a new lawyer successfully registers.
     * @param firm The new Firm entity that was just created.
     */
    @Transactional
    public void createTrialSubscriptionForFirm(Firm firm) {
        // 1. Find the predefined "TRIAL" plan in the database.
        // If this fails, it's a critical system configuration error, so we throw an exception.
        SubscriptionPlan trialPlan = planRepository.findByPlanName("TRIAL")
                .orElseThrow(() -> new IllegalStateException("TRIAL subscription plan not found in database."));

        // 2. Create a new Subscription entity.
        Subscription trialSubscription = new Subscription();
        trialSubscription.setFirm(firm);
        trialSubscription.setPlan(trialPlan);
        trialSubscription.setUser(null);
        trialSubscription.setStatus(SubscriptionStatus.TRIAL);

        // 3. Set the trial period (e.g., 14 days).
        Instant trialEndDate = Instant.now().plus(14, ChronoUnit.DAYS);
        trialSubscription.setEndDate(trialEndDate);

        // 4. Save the new subscription record to the database.
        subscriptionRepository.save(trialSubscription);

        System.out.println("Successfully created 14-day trial for firm: " + firm.getFirmName());
    }

//    @Transactional
//    public void createSubscriptionForResearcher(UUID id) {
//        SubscriptionPlan trialPlan = planRepository.findByPlanName("TRIAL_EDUCATION")
//                .orElseThrow(() -> new IllegalStateException("TRIAL subscription plan not found in database."));
//
//        // 2. Create a new Subscription entity.
//        Subscription trialSubscription = new Subscription();
//        trialSubscription.setPlan(trialPlan);
//        // we have to somehow get the data and then call that into the setUser
//        trialSubscription.setUser(null);
//        trialSubscription.setStatus(SubscriptionStatus.TRIAL);
//
//        // 3. Set the trial period (e.g., 14 days).
//        Instant trialEndDate = Instant.now().plus(7, ChronoUnit.DAYS);
//        trialSubscription.setEndDate(trialEndDate);
//
//        // 4. Save the new subscription record to the database.
//        subscriptionRepository.save(trialSubscription);
//
//        System.out.println("Successfully created 14-day trial for firm: " + firm.getFirmName());
//
//    }

}