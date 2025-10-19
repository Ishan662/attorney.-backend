package com.example.backend.payment.service;

import com.example.backend.model.firm.Firm;
import com.example.backend.model.subcription.Subscription; // <-- Import Subscription
import com.example.backend.model.subcription.SubscriptionStatus;
import com.example.backend.model.user.User;
import com.example.backend.payment.model.Payment;
import com.example.backend.payment.model.PaymentStatus;
import com.example.backend.payment.repository.PaymentRepository;
import com.example.backend.repositories.FirmRepository;
import com.example.backend.repositories.SubscriptionRepository;
import com.example.backend.repositories.UserRepository;
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
import java.util.UUID;

@Service
public class WebhookService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FirmRepository firmRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    public WebhookService(PaymentRepository paymentRepository,
                          SubscriptionRepository subscriptionRepository,
                          FirmRepository firmRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.firmRepository = firmRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void handleEvent(Event event) {
        String eventType = event.getType();
        logger.info("Received Stripe event: {} with ID: {}", eventType, event.getId());

        // Use a switch for better readability
        switch (eventType) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;

            // --- ▼▼▼ ADDED NEW CASES FOR CANCELLATION ▼▼▼ ---
            case "customer.subscription.updated":
            case "customer.subscription.deleted":
                handleSubscriptionUpdateOrDelete(event);
                break;
            // --- ▲▲▲ ADDED NEW CASES FOR CANCELLATION ▲▲▲ ---

            case "checkout.session.expired":
            case "checkout.session.async_payment_failed":
                handlePaymentFailure(event);
                break;
            default:
                logger.warn("Unhandled event type: {}", eventType);
        }
    }

    // This method acts as a router for completed sessions
    private void handleCheckoutSessionCompleted(Event event) {
        Optional<Session> sessionOptional = getSessionFromEvent(event);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            if ("subscription".equalsIgnoreCase(session.getMode())) {
                handleSubscriptionSuccess(session);
            } else if ("payment".equalsIgnoreCase(session.getMode())) {
                handleOneTimePaymentSuccess(session);
            }
        }
    }

    // --- ▼▼▼ NEW METHOD TO HANDLE CANCELLATION EVENTS ▼▼▼ ---
    private void handleSubscriptionUpdateOrDelete(Event event) {
        StripeObject stripeObject;
        try {
            stripeObject = event.getDataObjectDeserializer().deserializeUnsafe();
        } catch (JsonSyntaxException e) {
            logger.error("Failed to parse webhook JSON for subscription update event: {}", event.getId(), e);
            return;
        } catch (EventDataObjectDeserializationException e) {
            throw new RuntimeException(e);
        }

        // Check if the event object is a Subscription
        if (stripeObject instanceof com.stripe.model.Subscription) {
            com.stripe.model.Subscription stripeSubscription = (com.stripe.model.Subscription) stripeObject;

            // Find our local subscription record using the Stripe ID
            Optional<Subscription> localSubscriptionOpt =
                    subscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId());

            if (localSubscriptionOpt.isPresent()) {
                Subscription localSubscription = localSubscriptionOpt.get();

                // If Stripe's status is "canceled", update our local record
                if ("canceled".equals(stripeSubscription.getStatus())) {
                    if (localSubscription.getStatus() != SubscriptionStatus.CANCELLED) {
                        localSubscription.setStatus(SubscriptionStatus.CANCELLED);
                        subscriptionRepository.save(localSubscription);
                        logger.info("✅ Webhook updated subscription {} to CANCELLED.", stripeSubscription.getId());
                    }
                }
                // You could add more logic here to handle other statuses like "past_due"
            } else {
                logger.warn("Received subscription update webhook for a subscription not found in our DB: {}", stripeSubscription.getId());
            }
        }
    }
    // --- ▲▲▲ NEW METHOD TO HANDLE CANCELLATION EVENTS ▲▲▲ ---

    private void handleSubscriptionSuccess(Session session) {
        String referenceIdStr = session.getClientReferenceId();
        String stripeSubscriptionId = session.getSubscription();
        String stripeCustomerId = session.getCustomer();
        String referenceType = session.getMetadata() != null ? session.getMetadata().get("reference_type") : null;

        if (referenceIdStr == null || stripeSubscriptionId == null || referenceType == null) {
            logger.error("Webhook for subscription missing critical info: referenceId, subscriptionId, or referenceType");
            return;
        }

        UUID referenceId = UUID.fromString(referenceIdStr);
        Subscription subscription;

        if ("firm".equals(referenceType)) {
            subscription = subscriptionRepository.findByFirmId(referenceId).orElse(null);
            Firm firm = firmRepository.findById(referenceId).orElse(null);
            if (firm != null && (firm.getStripeCustomerId() == null || firm.getStripeCustomerId().isEmpty())) {
                firm.setStripeCustomerId(stripeCustomerId);
                firmRepository.save(firm);
            }
        } else if ("user".equals(referenceType)) {
            subscription = subscriptionRepository.findByUserId(referenceId).orElse(null);
            User user = userRepository.findById(referenceId).orElse(null);
            if (user != null && (user.getStripeCustomerId() == null || user.getStripeCustomerId().isEmpty())) {
                user.setStripeCustomerId(stripeCustomerId);
                userRepository.save(user);
            }
        } else {
            logger.error("Unknown reference_type in subscription webhook metadata: {}", referenceType);
            return;
        }

        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStripeSubscriptionId(stripeSubscriptionId);
            subscriptionRepository.save(subscription);
            logger.info("✅ Subscription for {} ID {} successfully activated. Status: ACTIVE", referenceType, referenceId);
        } else {
            logger.error("Could not find subscription for {} ID: {}", referenceType, referenceId);
        }
    }

    private void handleOneTimePaymentSuccess(Session session) {
        String sessionId = session.getId();
        Payment payment = paymentRepository.findBySessionId(sessionId);
        if (payment != null && payment.getStatus() == PaymentStatus.CREATED) {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            logger.info("✅ One-time payment succeeded for session ID: {}", sessionId);
        } else if (payment != null) {
            logger.warn("Received duplicate success event for one-time payment on session ID: {}", sessionId);
        } else {
            logger.error("Could not find one-time payment record for session ID: {}", sessionId);
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
                logger.info("❌ One-time payment failed for session ID: {}", sessionId);
            }
        }
    }

    private Optional<Session> getSessionFromEvent(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        try {
            stripeObject = dataObjectDeserializer.deserializeUnsafe();
        } catch (JsonSyntaxException | EventDataObjectDeserializationException e) {
            logger.error(">>>>>>>>>>>>> FAILED TO PARSE WEBHOOK JSON for event ID: {}. Error: {}", event.getId(), e.getMessage());
            return Optional.empty();
        }
        if (stripeObject instanceof Session) {
            return Optional.of((Session) stripeObject);
        } else {
            logger.error(">>>>>>>>>>>>> UNEXPECTED EVENT DATA TYPE. Expected Session, but got {}. Event ID: {}",
                    stripeObject != null ? stripeObject.getClass().getName() : "null", event.getId());
            return Optional.empty();
        }
    }
}