package com.java.eONE.controller;

import com.java.eONE.model.Topic;
import com.java.eONE.model.Chapter;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/topics")
public class TopicController {

    private final TopicService topicService;
    private final ChapterRepository chapterRepository;

    @Autowired
    public TopicController(TopicService topicService, ChapterRepository chapterRepository) {
        this.topicService = topicService;
        this.chapterRepository = chapterRepository;
    }

    @GetMapping
    public ResponseEntity<?> getTopicsByChapter(@RequestParam Long chapterId) {
        try {
            List<Topic> topics = topicService.getTopicsByChapterId(chapterId);
            return ResponseEntity.ok(Map.of("topics", topics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch topics: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        try {
            Optional<Topic> topic = topicService.getTopicById(id);
            return topic.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch topic: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTopic(@RequestBody Map<String, Object> body) {
        try {
            String name = (String) body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Topic name is required"));
            }

            Object chapterIdObj = body.get("chapter_id");
            if (chapterIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chapter ID is required"));
            }

            Long chapterId;
            try {
                if (chapterIdObj instanceof Number) {
                    chapterId = ((Number) chapterIdObj).longValue();
                } else {
                    chapterId = Long.valueOf(chapterIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid chapter ID format"));
            }

            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

            Topic topic = new Topic();
            topic.setName(name);
            topic.setChapter(chapter);
            
            if (body.get("description") != null) {
                topic.setDescription((String) body.get("description"));
            }
            if (body.get("display_order") != null) {
                Object displayOrderObj = body.get("display_order");
                if (displayOrderObj instanceof Number) {
                    topic.setDisplayOrder(((Number) displayOrderObj).intValue());
                }
            }

            Topic savedTopic = topicService.createTopic(topic);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Topic created successfully", "topic", savedTopic));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create topic: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Topic updatedTopic = new Topic();
            
            if (body.get("name") != null) {
                updatedTopic.setName((String) body.get("name"));
            }
            if (body.get("description") != null) {
                updatedTopic.setDescription((String) body.get("description"));
            }
            if (body.get("display_order") != null) {
                Object displayOrderObj = body.get("display_order");
                if (displayOrderObj instanceof Number) {
                    updatedTopic.setDisplayOrder(((Number) displayOrderObj).intValue());
                }
            }
            if (body.get("is_active") != null) {
                updatedTopic.setIsActive((Boolean) body.get("is_active"));
            }

            Topic savedTopic = topicService.updateTopic(id, updatedTopic);
            return ResponseEntity.ok(Map.of("message", "Topic updated successfully", "topic", savedTopic));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update topic: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        try {
            boolean deleted = topicService.deleteTopic(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Topic deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete topic: " + e.getMessage()));
        }
    }
}

