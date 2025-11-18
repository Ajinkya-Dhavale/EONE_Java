package com.java.eONE.service;

import com.java.eONE.model.Note;
import com.java.eONE.model.Chapter;
import com.java.eONE.model.User;
import com.java.eONE.repository.NoteRepository;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, ChapterRepository chapterRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
    }

    public List<Note> getNotesByChapterId(Long chapterId) {
        return noteRepository.findByChapterIdAndIsActiveTrueOrderByCreatedAtDesc(chapterId);
    }

    public List<Note> getAllNotesByChapterId(Long chapterId) {
        return noteRepository.findByChapterId(chapterId);
    }

    @Transactional
    public Note createNote(Note note) {
        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(note.getChapter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + note.getChapter().getId()));

        // Validate user exists
        User user = userRepository.findById(note.getUploadedBy().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + note.getUploadedBy().getId()));

        note.setChapter(chapter);
        note.setUploadedBy(user);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote) {
        Note existing = noteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));

        // Update fields
        if (updatedNote.getTitle() != null) {
            existing.setTitle(updatedNote.getTitle());
        }
        if (updatedNote.getDescription() != null) {
            existing.setDescription(updatedNote.getDescription());
        }
        if (updatedNote.getFileUrl() != null) {
            existing.setFileUrl(updatedNote.getFileUrl());
        }
        if (updatedNote.getFileName() != null) {
            existing.setFileName(updatedNote.getFileName());
        }
        if (updatedNote.getFileType() != null) {
            existing.setFileType(updatedNote.getFileType());
        }
        if (updatedNote.getFileSize() != null) {
            existing.setFileSize(updatedNote.getFileSize());
        }
        if (updatedNote.getIsActive() != null) {
            existing.setIsActive(updatedNote.getIsActive());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(existing);
    }

    @Transactional
    public boolean deleteNote(Long id) {
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }
}

