package com.example.backend.service;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import com.example.backend.repositories.DistanceServiceRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class MockDistanceService implements DistanceServiceRepository {

    @Override
    public TravelInfoDTO getTravelDuration(String origin, String destination){
        return new TravelInfoDTO(1800, "30 mins mocked");
    }
}
