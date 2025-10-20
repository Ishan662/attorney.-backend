//package com.example.backend.controllers;
//
//import com.example.backend.dto.CreateMeetingRequestDto;
//import com.example.backend.dto.MeetingRequestDto;
//import com.example.backend.service.MeetingRequestService;
//import jakarta.validation.Valid;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/meetings")
//public class MeetingRequestController {
//
//    private final MeetingRequestService service;
//
//    public MeetingRequestController(MeetingRequestService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public ResponseEntity<MeetingRequestDto> create(@Valid @RequestBody CreateMeetingRequestDto dto) {
//        MeetingRequestDto created = service.create(dto);
//        return ResponseEntity.ok(created);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<MeetingRequestDto> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(service.getById(id));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<MeetingRequestDto>> getAll() {
//        return ResponseEntity.ok(service.getAll());
//    }
//
//    @GetMapping("/by-date")
//    public ResponseEntity<List<MeetingRequestDto>> getByDate(
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//        return ResponseEntity.ok(service.getByDate(date));
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<MeetingRequestDto> updateStatus(@PathVariable Long id,
//                                                          @RequestParam String status) {
//        return ResponseEntity.ok(service.updateStatus(id, status));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        service.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//}
