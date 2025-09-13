package com.example.backend.service;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import org.springframework.web.client.RestTemplate;

public class GoogleDistanceService {
    private String GoogleApiKey;

    private static final  String DISTANCE_MATRIX_API_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TravelInfoDTO getTravelDuration(String Origin, String Destination){
        String url = DISTANCE_MATRIX_API_URL
                + "&origins=" + Origin
                + "&destinations=" + Destination
                + "&key=" + GoogleApiKey;

        GoogleDistanceMatrixResponse response =
                restTemplate.getForObject(url, GoogleDistanceMatrixResponse.class);

        if(response != null && "OK".equals(response.getStatus())){
            long durationInSeconds = response.getRows().get(0).getElements().get(0).getDuration().getValue();
            String text = response.getRows().get(0).getElements().get(0).getDuration().getText();
            return new TravelInfoDTO(durationInSeconds, text);
        }
        return new TravelInfoDTO(0, "Unavailable");
    }
}
