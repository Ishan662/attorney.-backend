package com.example.backend.repositories;

import com.example.backend.model.CalendarTasks.CalendarTasks;
import com.example.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarTasksRepository extends JpaRepository<CalendarTasks, UUID> {
    List<CalendarTasks> findByLawyer(User lawyer);
}
