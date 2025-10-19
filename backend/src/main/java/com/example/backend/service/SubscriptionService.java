package com.example.backend.service;

import com.example.backend.model.AppRole; // <-- FIX: ADDED IMPORT
import com.example.backend.model.firm.Firm;
import com.example.backend.model.subcription.Subscription;
import com.example.backend.model.subcription.SubscriptionPlan;
import com.example.backend.model.subcription.SubscriptionStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.FirmRepository;
import com.example.backend.repositories.SubscriptionPlanRepository;
import com.example.backend.repositories.SubscriptionRepository;
import com.example.backend.repositories.UserRepository;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;// <-- FIX: ADDED IMPORT
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // <-- FIX: ADDED IMPORT
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- FIX: ADDED IMPORT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final FirmRepository firmRepository;
    private final UserRepository userRepository;
    // --- ▼▼▼ FIX: REMOVED AuthService DEPENDENCY TO BREAK CIRCULAR DEPENDENCY ▼▼▼ ---
    // private final AuthService authService;

    @Autowired
    // --- ▼▼▼ FIX: UPDATED CONSTRUCTOR TO REMOVE AuthService ▼▼▼ ---
    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               SubscriptionPlanRepository planRepository,
                               FirmRepository firmRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.firmRepository = firmRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTrialSubscriptionForFirm(Firm firm) {
        // This method is correct, no changes needed
        SubscriptionPlan trialPlan = planRepository.findByPlanName("TRIAL")
                .orElseThrow(() -> new IllegalStateException("TRIAL subscription plan not found in database."));
        Subscription trialSubscription = new Subscription();
        trialSubscription.setFirm(firm);
        trialSubscription.setPlan(trialPlan);
        trialSubscription.setStatus(SubscriptionStatus.TRIAL);
        trialSubscription.setEndDate(Instant.now().plus(14, ChronoUnit.DAYS));
        subscriptionRepository.save(trialSubscription);
        System.out.println("Successfully created 14-day trial for firm: " + firm.getFirmName());
    }

    @Transactional
    public void createSubscriptionForResearcher(User researcher) {
        // This method is correct, no changes needed
        SubscriptionPlan researcherPlan = planRepository.findByPlanName("EDUCATOR")
                .orElseThrow(() -> new IllegalStateException("RESEARCHER_TRIAL plan not found in database."));
        Subscription subscription = new Subscription();
        subscription.setPlan(researcherPlan);
        subscription.setUser(researcher);
        subscription.setFirm(null);
        subscription.setStatus(SubscriptionStatus.TRIAL);
        subscription.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS));
        subscriptionRepository.save(subscription);
        System.out.println("Successfully created 7-day researcher trial for: " + researcher.getEmail());
    }

    @Transactional
    public String createSubscriptionCheckoutSession(Integer planId) throws StripeException {
        // --- ▼▼▼ FIX: CALL THE NEW LOCAL HELPER METHOD ▼▼▼ ---
        User currentUser = getCurrentUser();
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with ID: " + planId));

        String stripeCustomerId;
        String clientReferenceId;
        String referenceType;

        if (currentUser.getRole() == AppRole.LAWYER) {
            Firm firm = currentUser.getFirm();
            if (firm == null) {
                throw new IllegalStateException("Lawyer is not associated with a firm.");
            }
            stripeCustomerId = firm.getStripeCustomerId();
            if (stripeCustomerId == null) {
                stripeCustomerId = createStripeCustomer(firm.getFirmName(), currentUser.getEmail(), "firm_id", firm.getId().toString());
                firm.setStripeCustomerId(stripeCustomerId);
                firmRepository.save(firm);
            }
            clientReferenceId = firm.getId().toString();
            referenceType = "firm";

        } else if (currentUser.getRole() == AppRole.RESEARCHER) {
            stripeCustomerId = currentUser.getStripeCustomerId();
            if (stripeCustomerId == null) {
                String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                stripeCustomerId = createStripeCustomer(fullName, currentUser.getEmail(), "user_id", currentUser.getId().toString());
                currentUser.setStripeCustomerId(stripeCustomerId);
                userRepository.save(currentUser);
            }
            clientReferenceId = currentUser.getId().toString();
            referenceType = "user";

        } else {
            throw new IllegalStateException("User role is not eligible for subscriptions.");
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(stripeCustomerId)
                .setSuccessUrl("http://localhost:5173/lawyer/dashboard")
                .setCancelUrl("http://localhost:5173/lawyer/dashboard")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(plan.getStripePriceId())
                                .setQuantity(1L)
                                .build())
                .setClientReferenceId(clientReferenceId)
                .putMetadata("reference_type", referenceType)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    private String createStripeCustomer(String name, String email, String metadataKey, String metadataValue) throws StripeException {
        // This method is correct, no changes needed
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(name)
                .setEmail(email)
                .putMetadata(metadataKey, metadataValue)
                .build();
        Customer customer = Customer.create(params);
        return customer.getId();
    }


    /**
     * Cancels the subscription for the currently authenticated user.
     * It finds the user's subscription, tells Stripe to cancel it,
     * and updates the local status to CANCELLED.
     */
    @Transactional
    public void cancelSubscriptionForCurrentUser() throws StripeException {
        // 1. Get the current user
        User currentUser = getCurrentUser();
        com.example.backend.model.subcription.Subscription localSubscription;

        // 2. Find their subscription in our database
        if (currentUser.getRole() == AppRole.LAWYER) {
            localSubscription = subscriptionRepository.findByFirmId(currentUser.getFirm().getId())
                    .orElseThrow(() -> new IllegalStateException("No active subscription found for this firm."));
        } else if (currentUser.getRole() == AppRole.RESEARCHER) {
            localSubscription = subscriptionRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new IllegalStateException("No active subscription found for this user."));
        } else {
            throw new IllegalStateException("User role is not eligible for subscriptions.");
        }

        // 3. Check if there is a Stripe subscription to cancel
        String stripeSubscriptionId = localSubscription.getStripeSubscriptionId();
        if (stripeSubscriptionId == null || stripeSubscriptionId.isEmpty()) {
            throw new IllegalStateException("Cannot cancel subscription because it is not linked to Stripe.");
        }

        // 4. Tell Stripe to cancel the subscription
        com.stripe.model.Subscription stripeSubscription = com.stripe.model.Subscription.retrieve(stripeSubscriptionId);
        stripeSubscription.cancel();

        // 5. Update our local database immediately for instant user feedback
        localSubscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(localSubscription);

        logger.info("Subscription {} for {} ID {} was successfully canceled.",
                stripeSubscriptionId, currentUser.getRole(), currentUser.getId());
    }

    // --- ▼▼▼ FIX: ADDED THIS PRIVATE HELPER METHOD TO GET THE CURRENT USER ▼▼▼ ---
    /**
     * Retrieves the currently authenticated user from the security context.
     * This logic is placed here to break the circular dependency with AuthService.
     * @return The currently authenticated User entity.
     */
    private User getCurrentUser() {
        String firebaseUid = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in database for this operation."));
    }
}