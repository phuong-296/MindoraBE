package com.mindora.repository;

import com.mindora.entity.Expert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, UUID> {
    Page<Expert> findByIsVerifiedTrue(Pageable pageable);
    Optional<Expert> findByUserId(UUID userId);
}
