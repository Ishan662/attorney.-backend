package com.example.backend.controllers;

import com.example.backend.dto.userDTO.AdminUserViewDTO;
import com.example.backend.dto.userDTO.UpdateUserStatusRequest;
import com.example.backend.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')") // Secures all endpoints in this controller
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Autowired
    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserViewDTO>> getAllUsers() {
        List<AdminUserViewDTO> users = adminUserService.getAllUsersForAdminView();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<AdminUserViewDTO> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UpdateUserStatusRequest request) {

        AdminUserViewDTO updatedUser = adminUserService.updateUserStatus(userId, request.getNewStatus());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build(); // 204 No Content is standard for delete
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Long>> getUserCounts() {
        Map<String, Long> counts = adminUserService.getUserCounts();
        return ResponseEntity.ok(counts);
    }
}