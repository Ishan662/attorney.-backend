package com.example.backend.service;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.repositories.DistanceServiceRepository;
import com.example.backend.util.ValidationResult;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalenderValidationService {

    private final DistanceServiceRepository distanceServiceRepository;

    public CalenderValidationService(DistanceServiceRepository distanceServiceRepository){
        this.distanceServiceRepository = distanceServiceRepository;
    }

    public ValidationResult validateNewHearing(Hearing newHearing, List<Hearing> existingHearings) {
        LocalDateTime newStart = newHearing.getStartTime();
        LocalDateTime newEnd = newHearing.getEndTime();
        String newLocation = newHearing.getLocation();


        for (Hearing existing : existingHearings) {
            LocalDateTime existingStart = existing.getStartTime();
            LocalDateTime existingEnd = existing.getEndTime();
            String existingLocation = existing.getLocation();

            // 1. Time overlap check
            if (timesOverlap(newStart, newEnd, existingStart, existingEnd)) {
                return ValidationResult.fail("Hearing overlaps with an existing hearing.");
            }

            // 2. Travel feasibility check
            if (existingEnd.isBefore(newStart)) {
                TravelInfoDTO travelInfo = distanceServiceRepository.getTravelDuration(existingLocation, newLocation);
                if (travelInfo != null) {
                    return ValidationResult.ok(travelInfo.getSeconds(), travelInfo.getText());
                }
            }
        }

        return ValidationResult.ok();
    }

    /**
     * Utility: check if two time ranges overlap
     */
    private boolean timesOverlap(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
