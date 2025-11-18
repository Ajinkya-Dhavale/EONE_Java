package com.java.eONE.repository;

import com.java.eONE.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByChapterId(Long chapterId);
    List<Video> findByChapterIdAndIsActiveTrueOrderByCreatedAtDesc(Long chapterId);
    List<Video> findByUploadedById(Long uploadedById);
    long countByChapterId(Long chapterId);
}

