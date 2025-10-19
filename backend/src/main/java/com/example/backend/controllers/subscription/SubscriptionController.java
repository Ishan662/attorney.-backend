package com.example.backend.controllers.subscription;

import com.example.backend.dto.subscription.CreateSubscriptionRequestDto;
import com.example.backend.service.SubscriptionService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createSubscriptionCheckoutSession(@RequestBody CreateSubscriptionRequestDto request) {
        try {
            // This method will return the Stripe Checkout URL
            String checkoutUrl = subscriptionService.createSubscriptionCheckoutSession(request.getPlanId());
            return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body("Error creating Stripe session: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}