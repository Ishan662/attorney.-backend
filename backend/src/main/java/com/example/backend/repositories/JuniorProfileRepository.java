package com.example.backend.repositories;

import com.example.backend.model.junior.JuniorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface JuniorProfileRepository extends JpaRepository<JuniorProfile, UUID> {}
