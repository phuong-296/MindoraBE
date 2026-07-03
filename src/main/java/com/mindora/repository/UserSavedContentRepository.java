package com.mindora.repository;

import com.mindora.entity.UserSavedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSavedContentRepository extends JpaRepository<UserSavedContent, UUID> {
    List<UserSavedContent> findByUserIdOrderBySavedAtDesc(UUID userId);
    Optional<UserSavedContent> findByUserIdAndContentId(UUID userId, UUID contentId);
    boolean existsByUserIdAndContentId(UUID userId, UUID contentId);
    void deleteByUserIdAndContentId(UUID userId, UUID contentId);
}
