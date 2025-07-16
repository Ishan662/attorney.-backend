package com.example.backend.repositories;

import com.example.backend.model.AppRole;
import com.example.backend.model.UserStatus;
import com.example.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByFirebaseUid(String firebaseUid);
    Optional<User> findByEmail(String email);

    @Query("SELECT u.status FROM User u WHERE u.firebaseUid = :firebaseUid")
    Optional<UserStatus> findStatusByFirebaseUid(@Param("firebaseUid") String firebaseUid);
    List<User> findByFirmIdAndRole(UUID firmId, AppRole role);
}