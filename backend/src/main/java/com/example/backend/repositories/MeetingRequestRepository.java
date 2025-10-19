package com.example.backend.repositories;

import com.example.backend.model.MeetingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRequestRepository extends JpaRepository<MeetingRequest, Long> {

    List<MeetingRequest> findByMeetingDate(LocalDate date);

    // find overlapping meetings on a date
    List<MeetingRequest> findByMeetingDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDate date, java.time.LocalTime endTime, java.time.LocalTime startTime);
}