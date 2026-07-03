package com.mindora.repository;

import com.mindora.entity.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, UUID> {

    /**
     * Tìm chunks theo emotion tag (primary retrieval).
     * VD: emotion = "anxious" → lấy tất cả chunks tagged "anxious"
     */
    @Query("SELECT DISTINCT d FROM KnowledgeDocument d JOIN d.emotionTags t WHERE t = :emotion ORDER BY d.chunkIndex ASC")
    List<KnowledgeDocument> findByEmotionTag(@Param("emotion") String emotion);

    /**
     * Tìm chunks theo keyword (fallback retrieval).
     * Tìm trong title và content có chứa keyword.
     */
    @Query("SELECT d FROM KnowledgeDocument d WHERE " +
           "LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KnowledgeDocument> findByKeyword(@Param("keyword") String keyword);

    /**
     * Hybrid: emotion tag + keyword trong content.
     * Dùng khi muốn kết quả chính xác hơn.
     */
    @Query("SELECT DISTINCT d FROM KnowledgeDocument d JOIN d.emotionTags t WHERE " +
           "t IN :emotions AND (" +
           "  LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "  LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KnowledgeDocument> findByEmotionsAndKeyword(
            @Param("emotions") List<String> emotions,
            @Param("keyword") String keyword);
}
