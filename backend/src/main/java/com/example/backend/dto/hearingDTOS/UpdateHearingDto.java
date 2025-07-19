package com.example.backend.dto.hearingDTOS;

import com.example.backend.model.hearing.HearingStatus;
import java.time.Instant;

public class UpdateHearingDto {
    private String title;
    private Instant hearingDate;
    private String location;
    private String note;
    private HearingStatus status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Instant hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public HearingStatus getStatus() {
        return status;
    }

    public void setStatus(HearingStatus status) {
        this.status = status;
    }
}