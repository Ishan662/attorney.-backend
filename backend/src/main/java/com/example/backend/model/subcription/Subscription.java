// >> In a new file: model/Subscription.java
package com.example.backend.model.subcription;

import com.example.backend.model.firm.Firm;
import com.example.backend.model.subcription.SubscriptionPlan;
import com.example.backend.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.time.Instant;
import java.util.UUID;

// We need an enum for the status


@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Check(constraints = "(firm_id IS NULL AND user_id IS NOT NULL) OR (firm_id IS NOT NULL AND user_id IS NULL)")

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "firm_id")
    private Firm firm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private Instant endDate;

    // Getters and setters...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Firm getFirm() { return firm; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirm(Firm firm) { this.firm = firm; }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
}