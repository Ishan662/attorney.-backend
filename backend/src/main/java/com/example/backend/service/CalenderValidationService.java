package com.example.backend.service;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.util.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CalenderValidationService {

    @Value("${google.api.key}")
    private String googleApiKey; // Injected from application.properties

    private static final String DISTANCE_MATRIX_API_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetch travel duration using Google Distance Matrix API
     */
    public TravelInfoDTO getTravelDuration(String origin, String destination) {
        String url = DISTANCE_MATRIX_API_URL
                + "&origins=" + origin
                + "&destinations=" + destination
                + "&key=" + googleApiKey;

        GoogleDistanceMatrixResponse response =
                restTemplate.getForObject(url, GoogleDistanceMatrixResponse.class);

        if (response != null && "OK".equals(response.getStatus())) {
            long durationInSeconds = response.getRows().get(0).getElements().get(0).getDuration().getValue();
            String text = response.getRows().get(0).getElements().get(0).getDuration().getText();
            return new TravelInfoDTO(durationInSeconds, text);
        }
        return null;
    }

    /**
     * Validate a new hearing against existing ones (overlap + travel feasibility).
     */
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
                TravelInfoDTO travelInfo = getTravelDuration(existingLocation, newLocation);
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
