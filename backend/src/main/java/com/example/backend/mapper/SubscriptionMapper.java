// Create in a new file, e.g., mapper/SubscriptionMapper.java
package com.example.backend.mapper;

import com.example.backend.dto.subscription.SubscriptionResponseDto;
import com.example.backend.model.subcription.Subscription;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionMapper {

    public SubscriptionResponseDto toSubscriptionResponseDto(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        SubscriptionResponseDto dto = new SubscriptionResponseDto();
        dto.setId(subscription.getId());
        dto.setStatus(subscription.getStatus());
        dto.setEndDate(subscription.getEndDate());

        // This safely accesses the plan name without triggering other lazy-loaded fields
        if (subscription.getPlan() != null) {
            dto.setPlanName(subscription.getPlan().getPlanName());
        }

        return dto;
    }
}