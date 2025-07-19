// >> In a new file: service/EmailService.java
package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a simple plain-text email.
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param text The body of the email.
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // In a production app, you would use a logger here (e.g., SLF4J)
            System.err.println("Error sending email: " + e.getMessage());
            // It's often better not to throw an exception that stops the whole process,
            // but instead log the failure and maybe add it to a retry queue.
        }
    }
}