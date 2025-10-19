package com.example.backend.model.junior;

import com.example.backend.model.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "junior_profiles")
public class JuniorProfile {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // This makes the 'id' field both the primary key AND the foreign key
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 10, scale = 2) // Suitable for monetary values
    private BigDecimal monthlySalary;

    // No-arg constructor
    public JuniorProfile() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getMonthlySalary() { return monthlySalary; }
    public void setMonthlySalary(BigDecimal monthlySalary) { this.monthlySalary = monthlySalary; }
}