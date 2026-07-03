package com.mindora.repository;

import com.mindora.entity.ContentLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContentLibraryRepository extends JpaRepository<ContentLibrary, UUID> {
    Page<ContentLibrary> findByIsActiveTrueAndMoodTagAndContentType(
        String moodTag, String contentType, Pageable pageable);
    Page<ContentLibrary> findByIsActiveTrueAndMoodTag(String moodTag, Pageable pageable);
    Page<ContentLibrary> findByIsActiveTrueAndContentType(String contentType, Pageable pageable);
    Page<ContentLibrary> findByIsActiveTrue(Pageable pageable);
}
