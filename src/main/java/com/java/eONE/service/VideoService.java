package com.java.eONE.service;

import com.java.eONE.model.Video;
import com.java.eONE.model.Chapter;
import com.java.eONE.model.User;
import com.java.eONE.repository.VideoRepository;
import com.java.eONE.repository.ChapterRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, ChapterRepository chapterRepository, UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.chapterRepository = chapterRepository;
        this.userRepository = userRepository;
    }

    public List<Video> getVideosByChapterId(Long chapterId) {
        return videoRepository.findByChapterIdAndIsActiveTrueOrderByCreatedAtDesc(chapterId);
    }

    public List<Video> getAllVideosByChapterId(Long chapterId) {
        return videoRepository.findByChapterId(chapterId);
    }

    @Transactional
    public Video createVideo(Video video) {
        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(video.getChapter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + video.getChapter().getId()));

        // Validate user exists
        User user = userRepository.findById(video.getUploadedBy().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + video.getUploadedBy().getId()));

        // Auto-detect video type from URL if not provided
        if (video.getVideoType() == null || video.getVideoType().isEmpty()) {
            String videoUrl = video.getVideoUrl();
            if (videoUrl != null) {
                if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                    video.setVideoType("youtube");
                } else if (videoUrl.contains("vimeo.com")) {
                    video.setVideoType("vimeo");
                } else {
                    video.setVideoType("direct");
                }
            }
        }

        video.setChapter(chapter);
        video.setUploadedBy(user);
        video.setCreatedAt(LocalDateTime.now());
        video.setUpdatedAt(LocalDateTime.now());

        return videoRepository.save(video);
    }

    @Transactional
    public Video updateVideo(Long id, Video updatedVideo) {
        Video existing = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with id: " + id));

        // Update fields
        if (updatedVideo.getTitle() != null) {
            existing.setTitle(updatedVideo.getTitle());
        }
        if (updatedVideo.getDescription() != null) {
            existing.setDescription(updatedVideo.getDescription());
        }
        if (updatedVideo.getVideoUrl() != null) {
            existing.setVideoUrl(updatedVideo.getVideoUrl());
            // Auto-detect video type if changed
            String videoUrl = updatedVideo.getVideoUrl();
            if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
                existing.setVideoType("youtube");
            } else if (videoUrl.contains("vimeo.com")) {
                existing.setVideoType("vimeo");
            } else {
                existing.setVideoType("direct");
            }
        }
        if (updatedVideo.getVideoType() != null) {
            existing.setVideoType(updatedVideo.getVideoType());
        }
        if (updatedVideo.getThumbnailUrl() != null) {
            existing.setThumbnailUrl(updatedVideo.getThumbnailUrl());
        }
        if (updatedVideo.getDuration() != null) {
            existing.setDuration(updatedVideo.getDuration());
        }
        if (updatedVideo.getIsActive() != null) {
            existing.setIsActive(updatedVideo.getIsActive());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return videoRepository.save(existing);
    }

    @Transactional
    public boolean deleteVideo(Long id) {
        if (videoRepository.existsById(id)) {
            videoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }
}

