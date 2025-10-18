package com.example.backend.service;

import com.example.backend.dto.subscriptionDTOS.PlanDTO;
import com.example.backend.dto.subscriptionDTOS.PlanRequestDTO;
import com.example.backend.model.subcription.SubscriptionPlan;
import com.example.backend.repositories.SubscriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPlanService {

    private final SubscriptionPlanRepository planRepository;

    @Autowired
    public AdminPlanService(SubscriptionPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /**
     * Creates a new subscription plan.
     */
    @Transactional
    public PlanDTO createPlan(PlanRequestDTO requestDto) {
        // Optional: Check for duplicate plan names before saving
        if (planRepository.findByPlanName(requestDto.getPlanName()).isPresent()) {
            throw new IllegalStateException("A plan with this name already exists.");
        }

        SubscriptionPlan newPlan = new SubscriptionPlan();
        newPlan.setPlanName(requestDto.getPlanName());
        newPlan.setPriceMonthly(requestDto.getPriceMonthly());
        newPlan.setFeatures(requestDto.getFeatures());
        newPlan.setActive(requestDto.isActive());

        SubscriptionPlan savedPlan = planRepository.save(newPlan);
        return toDto(savedPlan);
    }

    /**
     * Updates an existing subscription plan.
     */
    @Transactional
    public PlanDTO updatePlan(Integer planId, PlanRequestDTO requestDto) {
        SubscriptionPlan existingPlan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));

        // Update fields from the request
        existingPlan.setPlanName(requestDto.getPlanName());
        existingPlan.setPriceMonthly(requestDto.getPriceMonthly());
        existingPlan.setFeatures(requestDto.getFeatures());
        existingPlan.setActive(requestDto.isActive());

        SubscriptionPlan updatedPlan = planRepository.save(existingPlan);
        return toDto(updatedPlan);
    }

    /**
     * Retrieves a single plan by its ID.
     */
    public PlanDTO getPlanById(Integer planId) {
        return planRepository.findById(planId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Plan not found with ID: " + planId));
    }

    /**
     * Retrieves a list of all available subscription plans.
     */
    public List<PlanDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Private helper method to convert an entity to a DTO (manual mapping)
    private PlanDTO toDto(SubscriptionPlan entity) {
        PlanDTO dto = new PlanDTO();
        dto.setId(entity.getId());
        dto.setPlanName(entity.getPlanName());
        dto.setPriceMonthly(entity.getPriceMonthly());
        dto.setFeatures(entity.getFeatures());
        dto.setActive(entity.isActive());
        return dto;
    }
}