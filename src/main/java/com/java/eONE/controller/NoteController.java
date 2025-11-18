package com.java.eONE.controller;

import com.java.eONE.model.Note;
import com.java.eONE.model.Chapter;
import com.java.eONE.model.User;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notes")
public class NoteController {

    private final NoteService noteService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteController(NoteService noteService, ChapterRepository chapterRepository, UserRepository userRepository) {
        this.noteService = noteService;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getNotesByChapter(@RequestParam Long chapterId) {
        try {
            List<Note> notes = noteService.getNotesByChapterId(chapterId);
            return ResponseEntity.ok(Map.of("notes", notes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch notes: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Optional<Note> note = noteService.getNoteById(id);
            return note.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch note: " + e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadNote(
            @RequestParam("title") String title,
            @RequestParam("chapter_id") Long chapterId,
            @RequestParam("uploaded_by") Long uploadedById,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Note title is required"));
            }

            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

            User user = userRepository.findById(uploadedById)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + uploadedById));

            Note note = new Note();
            note.setTitle(title);
            note.setChapter(chapter);
            note.setUploadedBy(user);
            note.setDescription(description);

            // Handle file upload if provided
            if (file != null && !file.isEmpty()) {
                String uploadDir = "uploads/notes";
                File directory = new File(uploadDir);
                if (!directory.exists()) directory.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    note.setFileUrl("/uploads/notes/" + fileName);
                    note.setFileName(file.getOriginalFilename());
                    note.setFileType(getFileExtension(file.getOriginalFilename()));
                    note.setFileSize(file.getSize());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "File upload failed: " + e.getMessage()));
                }
            }

            Note savedNote = noteService.createNote(note);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Note uploaded successfully", "note", savedNote));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload note: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Note updatedNote = new Note();
            
            if (body.get("title") != null) {
                updatedNote.setTitle((String) body.get("title"));
            }
            if (body.get("description") != null) {
                updatedNote.setDescription((String) body.get("description"));
            }
            if (body.get("is_active") != null) {
                updatedNote.setIsActive((Boolean) body.get("is_active"));
            }

            Note savedNote = noteService.updateNote(id, updatedNote);
            return ResponseEntity.ok(Map.of("message", "Note updated successfully", "note", savedNote));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update note: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            boolean deleted = noteService.deleteNote(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Note deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete note: " + e.getMessage()));
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }
}

