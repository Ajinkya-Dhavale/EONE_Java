package com.java.eONE.repository;

import com.java.eONE.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByChapterId(Long chapterId);
    List<Note> findByChapterIdAndIsActiveTrueOrderByCreatedAtDesc(Long chapterId);
    List<Note> findByUploadedById(Long uploadedById);
    long countByChapterId(Long chapterId);
}

