package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "meeting_requests")
public class MeetingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String location;

    @Column(nullable = false)
    private LocalDate meetingDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(length = 1000)
    private String guests;

    @Column(length = 2000)
    private String specialNote;

    private String googleMeetLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetingStatus status;

    private String createdBy;
    private String updatedBy;

    private Instant createdAt;
    private Instant updatedAt;

    // ------------------------------------
    // 1. Constructors (Equivalent of @NoArgsConstructor and @AllArgsConstructor)
    // ------------------------------------

    public MeetingRequest() {
    }

    public MeetingRequest(Long id, String title, String location, LocalDate meetingDate, LocalTime startTime, LocalTime endTime, String guests, String specialNote, String googleMeetLink, MeetingStatus status, String createdBy, String updatedBy, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.meetingDate = meetingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.guests = guests;
        this.specialNote = specialNote;
        this.googleMeetLink = googleMeetLink;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ------------------------------------
    // 2. Lifecycle Hooks (@PrePersist and @PreUpdate)
    // ------------------------------------

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) status = MeetingStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    // ------------------------------------
    // 3. Getters (Equivalent of @Getter)
    // ------------------------------------

    public Long getId() {
        return id;
    }

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

    public MeetingStatus getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ------------------------------------
    // 4. Setters (Equivalent of @Setter)
    // ------------------------------------

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ------------------------------------
    // 5. Equals and HashCode (Often provided by @EqualsAndHashCode in Lombok)
    // ------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingRequest that = (MeetingRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}