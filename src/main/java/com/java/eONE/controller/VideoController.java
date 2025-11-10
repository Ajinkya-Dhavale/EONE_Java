package com.java.eONE.controller;

import com.java.eONE.model.Video;
import com.java.eONE.model.Chapter;
import com.java.eONE.model.User;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Autowired
    public VideoController(VideoService videoService, ChapterRepository chapterRepository, UserRepository userRepository) {
        this.videoService = videoService;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getVideosByChapter(@RequestParam Long chapterId) {
        try {
            List<Video> videos = videoService.getVideosByChapterId(chapterId);
            return ResponseEntity.ok(Map.of("videos", videos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch videos: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable Long id) {
        try {
            Optional<Video> video = videoService.getVideoById(id);
            return video.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch video: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createVideo(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Video title is required"));
            }

            String videoUrl = (String) body.get("video_url");
            if (videoUrl == null || videoUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Video URL is required"));
            }

            Object chapterIdObj = body.get("chapter_id");
            if (chapterIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Chapter ID is required"));
            }

            Object uploadedByIdObj = body.get("uploaded_by");
            if (uploadedByIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Uploaded by user ID is required"));
            }

            Long chapterId;
            Long uploadedById;
            try {
                if (chapterIdObj instanceof Number) {
                    chapterId = ((Number) chapterIdObj).longValue();
                } else {
                    chapterId = Long.valueOf(chapterIdObj.toString());
                }
                
                if (uploadedByIdObj instanceof Number) {
                    uploadedById = ((Number) uploadedByIdObj).longValue();
                } else {
                    uploadedById = Long.valueOf(uploadedByIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID format"));
            }

            Chapter chapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

            User user = userRepository.findById(uploadedById)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + uploadedById));

            Video video = new Video();
            video.setTitle(title);
            video.setVideoUrl(videoUrl);
            video.setChapter(chapter);
            video.setUploadedBy(user);
            
            if (body.get("description") != null) {
                video.setDescription((String) body.get("description"));
            }
            if (body.get("video_type") != null) {
                video.setVideoType((String) body.get("video_type"));
            }
            if (body.get("thumbnail_url") != null) {
                video.setThumbnailUrl((String) body.get("thumbnail_url"));
            }
            if (body.get("duration") != null) {
                Object durationObj = body.get("duration");
                if (durationObj instanceof Number) {
                    video.setDuration(((Number) durationObj).intValue());
                }
            }

            Video savedVideo = videoService.createVideo(video);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Video created successfully", "video", savedVideo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create video: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVideo(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Video updatedVideo = new Video();
            
            if (body.get("title") != null) {
                updatedVideo.setTitle((String) body.get("title"));
            }
            if (body.get("description") != null) {
                updatedVideo.setDescription((String) body.get("description"));
            }
            if (body.get("video_url") != null) {
                updatedVideo.setVideoUrl((String) body.get("video_url"));
            }
            if (body.get("video_type") != null) {
                updatedVideo.setVideoType((String) body.get("video_type"));
            }
            if (body.get("thumbnail_url") != null) {
                updatedVideo.setThumbnailUrl((String) body.get("thumbnail_url"));
            }
            if (body.get("duration") != null) {
                Object durationObj = body.get("duration");
                if (durationObj instanceof Number) {
                    updatedVideo.setDuration(((Number) durationObj).intValue());
                }
            }
            if (body.get("is_active") != null) {
                updatedVideo.setIsActive((Boolean) body.get("is_active"));
            }

            Video savedVideo = videoService.updateVideo(id, updatedVideo);
            return ResponseEntity.ok(Map.of("message", "Video updated successfully", "video", savedVideo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update video: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id) {
        try {
            boolean deleted = videoService.deleteVideo(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Video deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete video: " + e.getMessage()));
        }
    }
}

