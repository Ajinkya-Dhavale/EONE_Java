package com.java.eONE.controller;

import com.java.eONE.model.Chapter;
import com.java.eONE.model.Subject;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/chapters")
public class ChapterController {

    private final ChapterService chapterService;
    private final SubjectRepository subjectRepository;

    @Autowired
    public ChapterController(ChapterService chapterService, SubjectRepository subjectRepository) {
        this.chapterService = chapterService;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public ResponseEntity<?> getChaptersBySubject(@RequestParam Long subjectId) {
        try {
            List<Chapter> chapters = chapterService.getChaptersBySubjectId(subjectId);
            return ResponseEntity.ok(Map.of("chapters", chapters));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch chapters: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChapterById(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            return chapter.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch chapter: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createChapter(@RequestBody Map<String, Object> body) {
        try {
            // Validate required fields
            String name = (String) body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chapter name is required"));
            }

            Object subjectIdObj = body.get("subject_id");
            if (subjectIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject ID is required"));
            }

            Long subjectId;
            try {
                if (subjectIdObj instanceof Number) {
                    subjectId = ((Number) subjectIdObj).longValue();
                } else {
                    subjectId = Long.valueOf(subjectIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid subject ID format"));
            }

            // Find subject
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found with id: " + subjectId));

            // Create chapter
            Chapter chapter = new Chapter();
            chapter.setName(name);
            chapter.setSubject(subject);
            
            if (body.get("description") != null) {
                chapter.setDescription((String) body.get("description"));
            }
            if (body.get("chapter_number") != null) {
                Object chapterNumberObj = body.get("chapter_number");
                if (chapterNumberObj instanceof Number) {
                    chapter.setChapterNumber(((Number) chapterNumberObj).intValue());
                }
            }
            if (body.get("display_order") != null) {
                Object displayOrderObj = body.get("display_order");
                if (displayOrderObj instanceof Number) {
                    chapter.setDisplayOrder(((Number) displayOrderObj).intValue());
                }
            }
            
            // Ensure isActive is set (default to true for new chapters)
            if (body.get("is_active") != null) {
                Object isActiveObj = body.get("is_active");
                if (isActiveObj instanceof Boolean) {
                    chapter.setIsActive((Boolean) isActiveObj);
                }
            } else {
                chapter.setIsActive(true); // Default to active for new chapters
            }

            Chapter savedChapter = chapterService.createChapter(chapter);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Chapter created successfully", "chapter", savedChapter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create chapter: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChapter(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Chapter updatedChapter = new Chapter();
            
            if (body.get("name") != null) {
                updatedChapter.setName((String) body.get("name"));
            }
            if (body.get("description") != null) {
                updatedChapter.setDescription((String) body.get("description"));
            }
            if (body.get("chapter_number") != null) {
                Object chapterNumberObj = body.get("chapter_number");
                if (chapterNumberObj instanceof Number) {
                    updatedChapter.setChapterNumber(((Number) chapterNumberObj).intValue());
                }
            }
            if (body.get("display_order") != null) {
                Object displayOrderObj = body.get("display_order");
                if (displayOrderObj instanceof Number) {
                    updatedChapter.setDisplayOrder(((Number) displayOrderObj).intValue());
                }
            }
            if (body.get("is_active") != null) {
                updatedChapter.setIsActive((Boolean) body.get("is_active"));
            }

            Chapter savedChapter = chapterService.updateChapter(id, updatedChapter);
            return ResponseEntity.ok(Map.of("message", "Chapter updated successfully", "chapter", savedChapter));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update chapter: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        try {
            boolean deleted = chapterService.deleteChapter(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Chapter deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete chapter: " + e.getMessage()));
        }
    }
}

