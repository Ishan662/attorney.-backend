package com.example.backend.repositories;

import com.example.backend.model.requests.Request;
import com.example.backend.model.requests.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ClientDashboardRepository extends JpaRepository<Request, UUID> {

    @Query("SELECT r FROM Request r " +
            "WHERE r.createdByClient.id = :clientId " +
            "AND r.meetingDate >= :today " +
            "AND r.status = :status " +
            "ORDER BY r.meetingDate ASC, r.startTime ASC")
    List<Request> findUpcomingMeetingsForClient(
            @Param("clientId") UUID clientId,
            @Param("today") LocalDate today,
            @Param("status") RequestStatus status
    );
}
