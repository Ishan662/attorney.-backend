package com.example.backend.repositories;

import com.example.backend.model.firm.Firm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FirmRepository extends JpaRepository<Firm, UUID> {
}