package com.mindora.repository;

import com.mindora.entity.MentalHealthAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentalHealthAnalysisRepository extends JpaRepository<MentalHealthAnalysis, UUID> {
    Optional<MentalHealthAnalysis> findFirstByUserIdOrderByAnalyzedAtDesc(UUID userId);
    List<MentalHealthAnalysis> findByUserIdOrderByAnalyzedAtDesc(UUID userId);
}
