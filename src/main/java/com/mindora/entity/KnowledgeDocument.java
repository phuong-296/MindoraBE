package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Bảng lưu các đoạn kiến thức tâm lý (knowledge chunks) dùng cho RAG.
 * Mỗi chunk là 1 đoạn văn khoảng 200-400 từ từ tài liệu gốc (PFA, WHO, CBT...).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "knowledge_documents")
public class KnowledgeDocument extends BaseEntity {

    @Column(nullable = false, length = 300)
    private String title;

    /** Nội dung chunk — đưa vào RAG context */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Nguồn tài liệu: PFA, WHO, CBT, Mindfulness... */
    @Column(length = 100)
    private String source;

    /**
     * Emotion tags để match nhanh với detectedEmotion của user.
     * VD: ["anxious","stressed","panic"]
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "knowledge_document_tags",
                     joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    private List<String> emotionTags;

    /** Từ khoá phụ để keyword matching */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "knowledge_document_keywords",
                     joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    /** Thứ tự chunk trong tài liệu gốc (để sắp xếp khi hiển thị nguồn) */
    @Column(name = "chunk_index")
    private Integer chunkIndex = 0;
}
