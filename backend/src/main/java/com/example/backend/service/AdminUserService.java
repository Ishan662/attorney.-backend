package com.example.backend.service;

import com.example.backend.dto.dashboardDTOS.AdminDashboardStatsDTO;
import com.example.backend.dto.userDTO.AdminUserViewDTO;
import com.example.backend.model.AppRole;
import com.example.backend.model.UserStatus;
import com.example.backend.model.user.User;
import com.example.backend.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    @Autowired
    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Fetches all users and converts them into the detailed DTO for the admin view.
     */
    public List<AdminUserViewDTO> getAllUsersForAdminView() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(this::mapUserToAdminViewDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of a user (e.g., from ACTIVE to INACTIVE).
     */
    @Transactional
    public AdminUserViewDTO updateUserStatus(UUID userId, UserStatus newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Prevent admins from deactivating themselves or other admins as a safety measure.
        if (user.getRole() == AppRole.ADMIN) {
            throw new IllegalArgumentException("Cannot change the status of an Admin user.");
        }

        user.setStatus(newStatus);
        User updatedUser = userRepository.save(user);
        return mapUserToAdminViewDto(updatedUser);
    }

    /**
     * Deletes a user from the database and Firebase.
     */
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getRole() == AppRole.ADMIN) {
            throw new IllegalArgumentException("Admin users cannot be deleted through this API.");
        }

        // Delete from our database. The 'cascade' settings on relationships will handle cleanup.
        userRepository.deleteById(userId);

        // Delete from Firebase Authentication
        try {
            FirebaseAuth.getInstance().deleteUser(user.getFirebaseUid());
        } catch (Exception e) {
            // Log this error. It's possible the user exists in our DB but not Firebase.
            // In a production system, you might want to handle this more gracefully.
            System.err.println("Could not delete user from Firebase. UID might be missing or invalid: " + e.getMessage());
        }
    }

    // A private helper to perform the complex mapping to our new DTO
    private AdminUserViewDTO mapUserToAdminViewDto(User user) {
        AdminUserViewDTO dto = new AdminUserViewDTO();
        dto.setId(user.getId());
        dto.setFullName((user.getFirstName() + " " + user.getLastName()).trim());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setDateJoined(user.getCreatedAt());

        // Add role-specific data
        if (user.getRole() == AppRole.LAWYER) {
            if (user.getFirm() != null) {
                dto.setFirmName(user.getFirm().getFirmName());
                // These are expensive queries (N+1 problem). For a real app, you would
                // optimize this with a custom repository query. For now, this works.
                dto.setJuniorLawyerCount(userRepository.countByFirmIdAndRole(user.getFirm().getId(), AppRole.JUNIOR));
                // Finding clients is harder as they aren't directly linked to a firm.
                // We will leave this as 0 for now to avoid complexity.
                dto.setClientCount(0L);
            }
        } else if (user.getRole() == AppRole.JUNIOR) {
            if (user.getFirm() != null) {
                // Find a senior lawyer in the same firm to display.
                userRepository.findFirstByFirmIdAndRole(user.getFirm().getId(), AppRole.LAWYER)
                        .ifPresent(lawyer -> dto.setSeniorLawyerName((lawyer.getFirstName() + " " + lawyer.getLastName()).trim()));
            }
        }

        // Note: Client and case counts require more complex queries involving the CaseMember table,
        // which can be added later as an optimization.

        return dto;
    }

    public Map<String, Long> getUserCounts() {
        Map<String, Long> userCounts = new HashMap<>();

        // Use our new, efficient repository method for each role
        userCounts.put("lawyers", userRepository.countByRole(AppRole.LAWYER));
        userCounts.put("juniors", userRepository.countByRole(AppRole.JUNIOR));
        userCounts.put("clients", userRepository.countByRole(AppRole.CLIENT));
        userCounts.put("researchers", userRepository.countByRole(AppRole.RESEARCHER));

        return userCounts;
    }

    public AdminDashboardStatsDTO getDashboardStatistics() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        // --- Calculate Lawyer Stats ---
        long totalLawyers = userRepository.countByRole(AppRole.LAWYER);
        long activeLawyers = userRepository.countByRoleAndStatus(AppRole.LAWYER, UserStatus.ACTIVE);
        stats.setTotalLawyers(totalLawyers);
        stats.setActiveLawyers(activeLawyers);
        stats.setInactiveLawyers(totalLawyers - activeLawyers);

        // --- Calculate Junior Stats ---
        stats.setTotalJuniors(userRepository.countByRole(AppRole.JUNIOR));
        stats.setActiveJuniors(userRepository.countByRoleAndStatus(AppRole.JUNIOR, UserStatus.ACTIVE));

        // --- Calculate Client Stats ---
        stats.setTotalClients(userRepository.countByRole(AppRole.CLIENT));
        stats.setActiveClients(userRepository.countByRoleAndStatus(AppRole.CLIENT, UserStatus.ACTIVE));

        // --- Calculate Researcher Stats ---
        stats.setTotalResearchers(userRepository.countByRole(AppRole.RESEARCHER));

        // --- Calculate Growth Metrics ---
        Instant oneMonthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        // We can count all new users, or just new lawyers. Let's count lawyers.
        long newLawyersThisMonth = userRepository.countByRoleAndCreatedAtAfter(AppRole.LAWYER, oneMonthAgo);
        stats.setNewSignupsThisMonth(newLawyersThisMonth);

        return stats;
    }


}