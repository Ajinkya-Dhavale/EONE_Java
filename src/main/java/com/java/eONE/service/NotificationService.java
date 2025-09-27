package com.java.eONE.service;

import com.java.eONE.DTO.NotificationMessageDTO;
import com.java.eONE.model.Notification;
import com.java.eONE.model.User;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.NotificationRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    private static final String STUDENT_ROLE_NAME = "STUDENT";

    public List<NotificationMessageDTO> getUserNotifications(Long userId, Integer limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of(); // return empty list instead of null

        List<Notification> notifications;

        if (STUDENT_ROLE_NAME.equals(user.getRole().getName())) {
            var classroom = user.getClassroom();
            var subjectIds = classroom.getSubjects().stream()
                                     .map(s -> s.getId())
                                     .collect(Collectors.toList());
            var assignmentIds = assignmentRepository.findBySubjectIdIn(subjectIds)
                                                    .stream().map(a -> a.getId())
                                                    .collect(Collectors.toList());

            notifications = notificationRepository.findByAssignmentIdInAndUserIsNullOrderByCreatedAtDesc(assignmentIds);
        } else {
            var assignmentIds = assignmentRepository.findByTeacherId(user.getId())
                                                    .stream().map(a -> a.getId())
                                                    .collect(Collectors.toList());

            notifications = notificationRepository.findByAssignmentIdInAndTeacherIsNullOrderByCreatedAtDesc(assignmentIds);
        }

        if (limit != null && notifications.size() > limit) {
            notifications = notifications.subList(0, limit);
        }

        // Map entity to DTO
        return notifications.stream()
                .map(n -> {
                    String type = n.getAssignment() != null ? "assignment" : "general";
                    return new NotificationMessageDTO(n.getMessage(), n.getCreatedAt(), type);
                })
                .collect(Collectors.toList());
    }
}
