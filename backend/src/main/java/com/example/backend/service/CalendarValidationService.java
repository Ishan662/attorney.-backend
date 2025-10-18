package com.example.backend.service;

import com.example.backend.dto.calendarValidationDTOS.TravelInfoDTO;
import com.example.backend.model.CalendarTasks.CalendarTasks;
import com.example.backend.model.hearing.Hearing;
import com.example.backend.model.user.User;
import com.example.backend.repositories.CalendarTasksRepository;
import com.example.backend.repositories.DistanceServiceRepository;
import com.example.backend.repositories.HearingRepository;
import com.example.backend.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarValidationService {

    private final DistanceServiceRepository distanceServiceRepository;
    private final HearingRepository hearingRepository;
    private final CalendarTasksRepository taskRepository;

    public CalendarValidationService(DistanceServiceRepository distanceServiceRepository,
                                     HearingRepository hearingRepository,
                                     CalendarTasksRepository taskRepository) {
        this.distanceServiceRepository = distanceServiceRepository;
        this.hearingRepository = hearingRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Validate a new hearing against existing hearings and tasks for the current user
     */
    public ValidationResult validateNewHearingWithTasks(LocalDateTime newStart,
                                                        LocalDateTime newEnd,
                                                        String newLocation,
                                                        User user) {

        if (newStart == null || newEnd == null || newLocation == null || user == null) {
            return ValidationResult.fail("Invalid input — missing required hearing data.");
        }

        List<LocalEvent> existingEvents = new ArrayList<>();

        // Fetch existing hearings
        List<Hearing> hearings = hearingRepository.findAllByLawyerId(user.getId());
        if (hearings != null) {
            for (Hearing h : hearings) {
                if (h.getStartTime() != null && h.getEndTime() != null && h.getLocation() != null) {
                    existingEvents.add(new LocalEvent(h.getStartTime(), h.getEndTime(), h.getLocation(), "Hearing"));
                }
            }
        }

        // Fetch existing tasks
        List<CalendarTasks> tasks = taskRepository.findByLawyer(user);
        if (tasks != null) {
            for (CalendarTasks t : tasks) {
                if (t.getStartTime() != null && t.getEndTime() != null && t.getLocation() != null) {
                    existingEvents.add(new LocalEvent(t.getStartTime(), t.getEndTime(), t.getLocation(), "Task"));
                }
            }
        }

        // Validate against all existing events
        for (LocalEvent event : existingEvents) {
            // 1. Overlap check
            if (timesOverlap(newStart, newEnd, event.start, event.end)) {
                return ValidationResult.fail("New hearing overlaps with an existing " + event.type);
            }

            // 2. Travel feasibility check
            if (event.end.isBefore(newStart)) {
                TravelInfoDTO travelInfo = distanceServiceRepository.getTravelDuration(event.location, newLocation);

                if (travelInfo != null) {
                    long availableTime = Duration.between(event.end, newStart).getSeconds();
                    if (travelInfo.getSeconds() > availableTime) {
                        return ValidationResult.fail(
                                "Not enough travel time from " + event.location + " to " + newLocation +
                                        ". Required: " + travelInfo.getText() +
                                        ", Available: " + availableTime / 60 + " mins"
                        );
                    }
                }
            }
        }

        // All checks passed
        return ValidationResult.ok("Validation successful — no conflicts found");
    }

    /**
     * Validate a new task against existing hearings and tasks
     */
    public ValidationResult validateNewTaskWithHearings(LocalDateTime newStart,
                                                        LocalDateTime newEnd,
                                                        String newLocation,
                                                        User user) {
        return validateNewHearingWithTasks(newStart, newEnd, newLocation, user);
    }

    /**
     * Utility: check if two time ranges overlap
     */
    private boolean timesOverlap(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) return false;
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Internal helper class for unified event handling
     */
    private static class LocalEvent {
        LocalDateTime start;
        LocalDateTime end;
        String location;
        String type;

        LocalEvent(LocalDateTime start, LocalDateTime end, String location, String type) {
            this.start = start;
            this.end = end;
            this.location = location;
            this.type = type;
        }
    }
}
