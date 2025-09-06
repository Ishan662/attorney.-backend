package com.example.backend.controllers;

import com.example.backend.dto.subscriptionDTOS.PlanDTO;
import com.example.backend.dto.subscriptionDTOS.PlanRequestDTO;
import com.example.backend.service.AdminPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/plans")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {

    private final AdminPlanService adminPlanService;

    @Autowired
    public AdminPlanController(AdminPlanService adminPlanService) {
        this.adminPlanService = adminPlanService;
    }

    @PostMapping
    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanRequestDTO planRequest) {
        PlanDTO createdPlan = adminPlanService.createPlan(planRequest);
        return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        List<PlanDTO> plans = adminPlanService.getAllPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable Integer planId) {
        PlanDTO plan = adminPlanService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable Integer planId, @RequestBody PlanRequestDTO planRequest) {
        PlanDTO updatedPlan = adminPlanService.updatePlan(planId, planRequest);
        return ResponseEntity.ok(updatedPlan);
    }


}