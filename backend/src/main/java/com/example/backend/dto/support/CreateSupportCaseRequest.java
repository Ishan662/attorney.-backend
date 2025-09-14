package com.example.backend.dto.support;

public class CreateSupportCaseRequest {
    private String subject;
    private String firstMessage;

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getFirstMessage() { return firstMessage; }
    public void setFirstMessage(String message) { this.firstMessage = message; }
}