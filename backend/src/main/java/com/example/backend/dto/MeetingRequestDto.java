package com.example.backend.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRequestDto {

    private Long id;
    private String title;
    private String location;
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String guests;
    private String specialNote;
    private String googleMeetLink;
    private String status;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
