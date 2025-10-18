package com.example.backend.repositories;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;

public interface DistanceServiceRepository {
    TravelInfoDTO getTravelDuration(String origin, String Destination);
}
