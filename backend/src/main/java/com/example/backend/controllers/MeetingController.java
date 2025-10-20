package com.example.backend.controllers;

import com.example.backend.dto.meeting.ClientMeetingRequestDTO;
import com.example.backend.dto.meeting.LawyerMeetingUpdateDTO;
import com.example.backend.dto.meeting.MeetingResponseDTO;
import com.example.backend.service.MeetingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @Autowired
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<MeetingResponseDTO> createMeetingRequest(@Valid @RequestBody ClientMeetingRequestDTO request) {
        MeetingResponseDTO createdMeeting = meetingService.createRequestByClient(request);
        return new ResponseEntity<>(createdMeeting, HttpStatus.CREATED);
    }

    @PutMapping("/{requestId}")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<MeetingResponseDTO> updateMeetingRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody LawyerMeetingUpdateDTO request) {
        MeetingResponseDTO updatedMeeting = meetingService.updateRequestByLawyer(requestId, request);
        return ResponseEntity.ok(updatedMeeting);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LAWYER', 'CLIENT')")
    public ResponseEntity<List<MeetingResponseDTO>> getMyMeetings() {
        List<MeetingResponseDTO> meetings = meetingService.getMeetingsForCurrentUser();
        return ResponseEntity.ok(meetings);
    }
}