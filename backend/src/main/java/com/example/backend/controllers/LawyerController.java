package com.example.backend.controllers;


import com.example.backend.dto.lawyerDTOS.CourtColorsRequest;
import com.example.backend.model.lawyer.Lawyer;
import com.example.backend.service.LawyerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lawyers")
public class LawyerController {

    private final LawyerService lawyerService;

    public LawyerController(LawyerService lawyerService){
        this.lawyerService = lawyerService;
    }

    @PostMapping("/court-colors")
    public ResponseEntity<Lawyer> saveCourtColors(@RequestBody CourtColorsRequest request){
        Lawyer lawyer = lawyerService.saveOrUpdateCourtColors(request);
        return ResponseEntity.ok(lawyer);
    }
}
