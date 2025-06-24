package com.example.backend.repositories;

import com.example.backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByFirebaseUid(String firebaseUid);
}