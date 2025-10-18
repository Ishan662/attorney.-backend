package com.example.backend.payment.service;

import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    public WebhookService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public void handleEvent(Event event) {
        String eventType = event.getType();
        logger.info("Received Stripe event: {} with ID: {}", eventType, event.getId());

        switch (eventType) {
            case "checkout.session.completed":
                handlePaymentSuccess(event);
                break;
            case "checkout.session.expired":
            case "checkout.session.async_payment_failed":
                handlePaymentFailure(event);
                break;
            default:
                logger.warn("Unhandled event type: {}", eventType);
        }
    }

    private void handlePaymentSuccess(Event event) {
        Optional<Session> sessionOptional = getSessionFromEvent(event);
        if (sessionOptional.isPresent()) {
            String sessionId = sessionOptional.get().getId();
            Payment payment = paymentRepository.findBySessionId(sessionId);

            // Idempotency check: Only process if the payment is still in CREATED state.
            if (payment != null && payment.getStatus() == PaymentStatus.CREATED) {
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
                logger.info("✅ Payment succeeded for session ID: {}", sessionId);
                // Trigger post-payment actions here (e.g., notify user, grant access)
            } else {
                logger.warn("Received duplicate success event or payment not found for session ID: {}", sessionId);
            }
        }
    }

    private void handlePaymentFailure(Event event) {
        Optional<Session> sessionOptional = getSessionFromEvent(event);
        if (sessionOptional.isPresent()) {
            String sessionId = sessionOptional.get().getId();
            Payment payment = paymentRepository.findBySessionId(sessionId);

            if (payment != null && payment.getStatus() == PaymentStatus.CREATED) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                logger.info("❌ Payment failed for session ID: {}", sessionId);
            } else {
                logger.warn("Received duplicate failed event or payment not found for session ID: {}", sessionId);
            }
        }
    }

    private Optional<Session> getSessionFromEvent(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;

        try {
            // Try to deserialize. This is more explicit than checking Optional.
            stripeObject = dataObjectDeserializer.deserializeUnsafe();
        } catch (JsonSyntaxException | EventDataObjectDeserializationException e) {
            logger.error(">>>>>>>>>>>>> FAILED TO PARSE WEBHOOK JSON for event ID: {}. Error: {}", event.getId(), e.getMessage());
            return Optional.empty();
        }

        // Check if the deserialized object is of the expected type (Session)
        if (stripeObject instanceof Session) {
            return Optional.of((Session) stripeObject);
        } else {
            logger.error(">>>>>>>>>>>>> UNEXPECTED EVENT DATA TYPE. Expected Session, but got {}. Event ID: {}",
                    stripeObject != null ? stripeObject.getClass().getName() : "null",
                    event.getId());
            return Optional.empty();
        }
    }
}