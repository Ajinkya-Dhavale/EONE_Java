package com.java.eONE.repository;

import com.java.eONE.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByChapterId(Long chapterId);
    List<Topic> findByChapterIdAndIsActiveTrueOrderByDisplayOrder(Long chapterId);
    long countByChapterId(Long chapterId);
}

