package com.example.backend.dto.subscriptionDTOS;

import java.math.BigDecimal;
import java.util.Map;

public class PlanDTO {
    private Integer id;
    private String planName;
    private BigDecimal priceMonthly;
    private Map<String, Object> features;
    private boolean isActive;

    // --- Getters and Setters for all fields ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public BigDecimal getPriceMonthly() { return priceMonthly; }
    public void setPriceMonthly(BigDecimal priceMonthly) { this.priceMonthly = priceMonthly; }
    public Map<String, Object> getFeatures() { return features; }
    public void setFeatures(Map<String, Object> features) { this.features = features; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
