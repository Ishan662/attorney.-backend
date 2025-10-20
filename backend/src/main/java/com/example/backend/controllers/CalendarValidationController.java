package com.example.backend.controllers;

import com.example.backend.dto.calendarTasksDTOs.CalendarTaskRequestDTO;
import com.example.backend.dto.hearingDTOS.CreateHearingDto;
import com.example.backend.model.user.User;
import com.example.backend.service.CalendarValidationService;
import com.example.backend.service.CalendarTaskService;
import com.example.backend.service.HearingService;
import com.example.backend.util.ValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarValidationController {

    private final CalendarValidationService calendarValidationService;
    private final CalendarTaskService calendarTaskService;
    private final HearingService hearingService;

    public CalendarValidationController(
            CalendarValidationService calendarValidationService,
            CalendarTaskService calendarTaskService,
            HearingService hearingService) {
        this.calendarValidationService = calendarValidationService;
        this.calendarTaskService = calendarTaskService;
        this.hearingService = hearingService;
    }

    /**
     * Convert Instant to LocalDateTime using system default timezone
     */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;
    }

    /**
     * Validate a new hearing before creating it
     */
    @PostMapping("/validate/hearing")
    @PreAuthorize("hasAnyRole('LAWYER','JUNIOR')")
    public ResponseEntity<Map<String, Object>> validateHearing(@RequestBody CreateHearingDto hearingDto) {
        User currentUser = hearingService.getCurrentUser();

        LocalDateTime startTime = toLocalDateTime(hearingDto.getStartTime());
        LocalDateTime endTime = toLocalDateTime(hearingDto.getEndTime());

        ValidationResult result = calendarValidationService.validateNewHearingWithTasks(
                startTime,
                endTime,
                hearingDto.getLocation(),
                currentUser
        );

        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("message", result.getMessage());
        response.put("travelSeconds", result.getTravelSeconds());
        response.put("travelText", result.getTravelText());

        return ResponseEntity.ok(response);


    }
    private LocalDateTime toLocalDateTime(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Instant instant) return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        if (obj instanceof LocalDateTime ldt) return ldt;
        throw new IllegalArgumentException("Unsupported date type: " + obj.getClass());
    }

    /**
     * Validate a new task before creating it
     */
    @PostMapping("/validate/task")
    @PreAuthorize("hasAnyRole('LAWYER','JUNIOR')")
    public ResponseEntity<Map<String, Object>> validateTask(@RequestBody CalendarTaskRequestDTO taskDto) {
        User currentUser = calendarTaskService.getCurrentUser();

        LocalDateTime startTime = toLocalDateTime(taskDto.getStartTime());
        LocalDateTime endTime = toLocalDateTime(taskDto.getEndTime());

        ValidationResult result = calendarValidationService.validateNewTaskWithHearings(
                startTime,
                endTime,
                taskDto.getLocation(),
                currentUser
        );

        Map<String, Object> response = new HashMap<>();
        response.put("valid", result.isValid());
        response.put("message", result.getMessage());
        response.put("travelSeconds", result.getTravelSeconds());
        response.put("travelText", result.getTravelText());

        return ResponseEntity.ok(response);
    }
}
