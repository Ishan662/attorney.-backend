package com.example.backend.dto.caseDTOS;

import java.util.UUID;

/**
 * A lightweight DTO for populating a case selection dropdown.
 */
public class CaseSelectionDTO {
    private UUID id;
    private String caseTitle;
    private String caseNumber;

    // No-arg constructor
    public CaseSelectionDTO() {}

    // Constructor for easy mapping
    public CaseSelectionDTO(UUID id, String caseTitle, String caseNumber) {
        this.id = id;
        this.caseTitle = caseTitle;
        this.caseNumber = caseNumber;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
}