package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

    // --- Constructors ---

    // Equivalent of @NoArgsConstructor
    public CreateMeetingRequestDto() {
    }

    // Equivalent of @AllArgsConstructor
    public CreateMeetingRequestDto(
            String title,
            String location,
            LocalDate meetingDate,
            LocalTime startTime,
            LocalTime endTime,
            String guests,
            String specialNote,
            String googleMeetLink,
            String createdBy) {
        this.title = title;
        this.location = location;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.guests = guests;
        this.specialNote = specialNote;
        this.googleMeetLink = googleMeetLink;
        this.createdBy = createdBy;
    }

    // --- Getters ---

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getMeetingDate() {
        return meetingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getGuests() {
        return guests;
    }

    public String getSpecialNote() {
        return specialNote;
    }

    public String getGoogleMeetLink() {
        return googleMeetLink;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    // --- Setters ---

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMeetingDate(LocalDate meetingDate) {
        this.meetingDate = meetingDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public void setSpecialNote(String specialNote) {
        this.specialNote = specialNote;
    }

    public void setGoogleMeetLink(String googleMeetLink) {
        this.googleMeetLink = googleMeetLink;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}