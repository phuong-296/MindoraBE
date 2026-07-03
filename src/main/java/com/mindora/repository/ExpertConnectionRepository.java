package com.mindora.repository;

import com.mindora.entity.ExpertConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpertConnectionRepository extends JpaRepository<ExpertConnection, UUID> {
    List<ExpertConnection> findByUserIdOrderByRequestedAtDesc(UUID userId);
    List<ExpertConnection> findByExpertIdOrderByRequestedAtDesc(UUID expertId);
    Optional<ExpertConnection> findByIdAndExpertUserId(UUID id, UUID expertUserId);
}
