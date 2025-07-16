// >> In a new file: service/TwilioService.java
package com.example.backend.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.service.sid}")
    private String serviceSid;

    // This method runs once when the service is created, initializing the Twilio client.
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    /**
     * Sends a verification OTP to a given phone number using the Twilio Verify service.
     *
     * @param phoneNumber The user's phone number in E.164 format (e.g., "+94767501542").
     */
    public void sendVerificationOtp(String phoneNumber) {
        try {
            Verification.creator(
                    serviceSid,
                    phoneNumber,
                    "sms" // The channel to send the code on (can also be "call" or "email")
            ).create();
            System.out.println("Successfully requested OTP for: " + phoneNumber);
        } catch (ApiException e) {
            // Handle exceptions from Twilio, e.g., invalid phone number format
            throw new RuntimeException("Failed to send OTP via Twilio: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if the provided OTP code is valid for the given phone number.
     *
     * @param phoneNumber The user's phone number in E.164 format.
     * @param otpCode The 6-digit code the user entered.
     * @return true if the code is valid ("approved"), false otherwise.
     */
    public boolean checkVerificationOtp(String phoneNumber, String otpCode) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(
                    serviceSid
            ).setTo(phoneNumber).setCode(otpCode).create();

            // The verification is successful only if the status is "approved".
            // Other statuses are "pending" or "canceled".
            return "approved".equals(verificationCheck.getStatus());

        } catch (ApiException e) {
            // This often happens if the code is incorrect or expired.
            // We can log the error but return false to indicate verification failure.
            System.err.println("Twilio OTP check failed: " + e.getMessage());
            return false;
        }
    }
}