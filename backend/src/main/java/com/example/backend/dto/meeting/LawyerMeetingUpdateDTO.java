package com.example.backend.dto.meeting;

import com.example.backend.model.requests.RequestStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class LawyerMeetingUpdateDTO {
    @NotNull(message = "New status is required.")
    private RequestStatus newStatus;
    private LocalDate rescheduledDate;
    private LocalTime rescheduledStartTime;
    private LocalTime rescheduledEndTime;
    private String note;

    public RequestStatus getNewStatus() { return newStatus; }
    public void setNewStatus(RequestStatus newStatus) { this.newStatus = newStatus; }
    public LocalDate getRescheduledDate() { return rescheduledDate; }
    public void setRescheduledDate(LocalDate rescheduledDate) { this.rescheduledDate = rescheduledDate; }
    public LocalTime getRescheduledStartTime() { return rescheduledStartTime; }
    public void setRescheduledStartTime(LocalTime rescheduledStartTime) { this.rescheduledStartTime = rescheduledStartTime; }
    public LocalTime getRescheduledEndTime() { return rescheduledEndTime; }
    public void setRescheduledEndTime(LocalTime rescheduledEndTime) { this.rescheduledEndTime = rescheduledEndTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}