package com.java.eONE.service;

import com.java.eONE.model.Chapter;
import com.java.eONE.model.Subject;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository, SubjectRepository subjectRepository) {
        this.chapterRepository = chapterRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Chapter> getChaptersBySubjectId(Long subjectId) {
        return chapterRepository.findBySubjectIdAndIsActiveTrueOrderByDisplayOrder(subjectId);
    }

    public List<Chapter> getAllChaptersBySubjectId(Long subjectId) {
        return chapterRepository.findBySubjectId(subjectId);
    }

    @Transactional
    public Chapter createChapter(Chapter chapter) {
        // Validate subject exists
        Subject subject = subjectRepository.findById(chapter.getSubject().getId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + chapter.getSubject().getId()));

        chapter.setSubject(subject);
        chapter.setCreatedAt(LocalDateTime.now());
        chapter.setUpdatedAt(LocalDateTime.now());
        
        // Ensure isActive is set to true for new chapters
        if (chapter.getIsActive() == null) {
            chapter.setIsActive(true);
        }
        
        // Ensure displayOrder is set
        if (chapter.getDisplayOrder() == null) {
            chapter.setDisplayOrder(0);
        }
        
        // Check for duplicate chapter name in same subject
        Optional<Chapter> existing = chapterRepository.findBySubjectIdAndName(
                chapter.getSubject().getId(), chapter.getName());
        if (existing.isPresent() && !existing.get().getId().equals(chapter.getId())) {
            throw new IllegalArgumentException("Chapter with name '" + chapter.getName() + "' already exists in this subject");
        }

        return chapterRepository.save(chapter);
    }

    @Transactional
    public Chapter updateChapter(Long id, Chapter updatedChapter) {
        Chapter existing = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + id));

        // Update fields
        if (updatedChapter.getName() != null) {
            // Check for duplicate name in same subject
            Optional<Chapter> duplicate = chapterRepository.findBySubjectIdAndName(
                    existing.getSubject().getId(), updatedChapter.getName());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Chapter with name '" + updatedChapter.getName() + "' already exists in this subject");
            }
            existing.setName(updatedChapter.getName());
        }
        if (updatedChapter.getDescription() != null) {
            existing.setDescription(updatedChapter.getDescription());
        }
        if (updatedChapter.getChapterNumber() != null) {
            existing.setChapterNumber(updatedChapter.getChapterNumber());
        }
        if (updatedChapter.getDisplayOrder() != null) {
            existing.setDisplayOrder(updatedChapter.getDisplayOrder());
        }
        if (updatedChapter.getIsActive() != null) {
            existing.setIsActive(updatedChapter.getIsActive());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return chapterRepository.save(existing);
    }

    @Transactional
    public boolean deleteChapter(Long id) {
        if (chapterRepository.existsById(id)) {
            chapterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Chapter> getChapterById(Long id) {
        return chapterRepository.findById(id);
    }
}

