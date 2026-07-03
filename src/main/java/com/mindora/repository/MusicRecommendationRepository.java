package com.mindora.repository;

import com.mindora.entity.MusicRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MusicRecommendationRepository extends JpaRepository<MusicRecommendation, UUID> {
    List<MusicRecommendation> findByUserIdOrderByRecommendedAtDesc(UUID userId);
}
