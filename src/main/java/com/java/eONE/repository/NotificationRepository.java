package com.java.eONE.repository;

import com.java.eONE.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    List<Notification> findByAssignmentIdInAndUserIsNullOrderByCreatedAtDesc(List<Long> assignmentIds);

    List<Notification> findByAssignmentIdInAndTeacherIsNullOrderByCreatedAtDesc(List<Long> assignmentIds);
}
