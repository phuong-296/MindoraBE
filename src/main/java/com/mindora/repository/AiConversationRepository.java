package com.mindora.repository;

import com.mindora.entity.AiConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiConversationRepository extends JpaRepository<AiConversation, UUID> {
    List<AiConversation> findByUserIdAndIsArchivedFalseOrderByUpdatedAtDesc(UUID userId);
    Optional<AiConversation> findFirstByUserIdAndIsArchivedFalseOrderByUpdatedAtDesc(UUID userId);
}
