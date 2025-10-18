package com.example.backend.repositories;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import org.springframework.stereotype.Repository;

@Repository
public class DistanceServiceRepositoryImpl implements DistanceServiceRepository {

    @Override
    public TravelInfoDTO getTravelDuration(String origin, String destination) {
        // Dummy implementation — replace with real distance API if needed
        long dummySeconds = 1800; // 30 minutes
        String dummyText = "Approx. 30 mins travel";
        return new TravelInfoDTO(dummySeconds, dummyText);
    }
}
