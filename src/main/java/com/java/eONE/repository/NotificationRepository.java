package com.java.eONE.repository;

import com.java.eONE.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    // For students: get assignment notifications (teacher uploads) where user is null and teacher is not null
    List<Notification> findByAssignmentIdInAndUserIsNullAndTeacherIsNotNullOrderByCreatedAtDesc(List<Long> assignmentIds);

    // For teachers: get submission notifications where teacher is null and user is not null
    List<Notification> findByAssignmentIdInAndTeacherIsNullAndUserIsNotNullOrderByCreatedAtDesc(List<Long> assignmentIds);
    
    // For students: get personal notifications (grading notifications)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
