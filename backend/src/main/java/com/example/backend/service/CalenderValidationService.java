package com.example.backend.service;

import com.example.backend.model.hearing.Hearing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Service
public class CalenderValidationService {

    @Value("${google.api.key}")
    private String googleApiKey; // Injected from application.properties

    private final String DISTANCE_MATRIX_API_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    private final RestTemplate restTemplate = new RestTemplate();

    public Duration getTravelDuration(String origin, String destination) {
        String url = DISTANCE_MATRIX_API_URL
                + "&origins=" + origin
                + "&destinations=" + destination
                + "&key=" + googleApiKey;

        GoogleDistanceMatrixResponse response = restTemplate.getForObject(url, GoogleDistanceMatrixResponse.class);

        if (response != null && "OK".equals(response.getStatus())) {
            long durationInSeconds = response.getRows().get(0).getElements().get(0).getDuration().getValue();
            return Duration.ofSeconds(durationInSeconds);
        }
        return null;
    }

    // Rest of your validateHearings() method...
}
