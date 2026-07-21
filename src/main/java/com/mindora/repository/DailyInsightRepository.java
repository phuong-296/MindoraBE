package com.mindora.repository;

import com.mindora.entity.DailyInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyInsightRepository extends JpaRepository<DailyInsight, UUID> {

    Optional<DailyInsight> findByUserIdAndInsightDate(UUID userId, LocalDate insightDate);

    List<DailyInsight> findByUserIdOrderByInsightDateDesc(UUID userId);

    // Dùng cho "Tổng kết tuần" và "Bộ sưu tập huy hiệu" (giới hạn theo tháng hiện tại) — gom các
    // bản ghi trong khoảng ngày để tính XP/streak/tree tích lũy hoặc lọc achievement.
    List<DailyInsight> findByUserIdAndInsightDateBetween(UUID userId, LocalDate start, LocalDate end);
}
