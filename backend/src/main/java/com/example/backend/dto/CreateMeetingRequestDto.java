package com.example.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetingRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String location;

    @NotNull(message = "Meeting date is required")
    private LocalDate meetingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    // Optional
    private String guests;

    private String specialNote;

    // Optional pre-generated meet link (or leave null)
    @Pattern(regexp = "^(https?://).+$", message = "Google Meet Link must be a valid URL")
    private String googleMeetLink;

    // createdBy (client email or id)
    @Email(message = "createdBy must be a valid email")
    private String createdBy;
}
