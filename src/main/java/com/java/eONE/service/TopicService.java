package com.java.eONE.service;

import com.java.eONE.model.Topic;
import com.java.eONE.model.Chapter;
import com.java.eONE.repository.TopicRepository;
import com.java.eONE.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;

    @Autowired
    public TopicService(TopicRepository topicRepository, ChapterRepository chapterRepository) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
    }

    public List<Topic> getTopicsByChapterId(Long chapterId) {
        return topicRepository.findByChapterIdAndIsActiveTrueOrderByDisplayOrder(chapterId);
    }

    public List<Topic> getAllTopicsByChapterId(Long chapterId) {
        return topicRepository.findByChapterId(chapterId);
    }

    @Transactional
    public Topic createTopic(Topic topic) {
        // Validate chapter exists
        Chapter chapter = chapterRepository.findById(topic.getChapter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + topic.getChapter().getId()));

        topic.setChapter(chapter);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());

        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(Long id, Topic updatedTopic) {
        Topic existing = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with id: " + id));

        // Update fields
        if (updatedTopic.getName() != null) {
            existing.setName(updatedTopic.getName());
        }
        if (updatedTopic.getDescription() != null) {
            existing.setDescription(updatedTopic.getDescription());
        }
        if (updatedTopic.getDisplayOrder() != null) {
            existing.setDisplayOrder(updatedTopic.getDisplayOrder());
        }
        if (updatedTopic.getIsActive() != null) {
            existing.setIsActive(updatedTopic.getIsActive());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return topicRepository.save(existing);
    }

    @Transactional
    public boolean deleteTopic(Long id) {
        if (topicRepository.existsById(id)) {
            topicRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }
}

