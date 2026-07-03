package com.mindora.repository;

import com.mindora.entity.MoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MoodLogRepository extends JpaRepository<MoodLog, UUID> {
    Optional<MoodLog> findByUserIdAndLogDate(UUID userId, LocalDate logDate);
    List<MoodLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(UUID userId, LocalDate from, LocalDate to);
}
