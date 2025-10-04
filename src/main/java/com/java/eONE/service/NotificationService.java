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
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return List.of(); // return empty list instead of null
        }

        System.out.println("Getting notifications for user: " + user.getName() + " (Role: " + user.getRole().getName() + ")");
        List<Notification> notifications;

        if (STUDENT_ROLE_NAME.equals(user.getRole().getName())) {
            // For students, get notifications in two categories:
            // 1. Assignment notifications for their classroom subjects (teacher uploads)
            // 2. Personal notifications (grading notifications)
            var classroom = user.getClassroom();
            if (classroom != null) {
                var subjectIds = classroom.getSubjects().stream()
                                         .map(s -> s.getId())
                                         .collect(Collectors.toList());
                var assignmentIds = assignmentRepository.findBySubjectIdIn(subjectIds)
                                                        .stream().map(a -> a.getId())
                                                        .collect(Collectors.toList());

                // Get assignment notifications (teacher uploads) and personal notifications (grading)
                var assignmentNotifications = notificationRepository.findByAssignmentIdInAndUserIsNullAndTeacherIsNotNullOrderByCreatedAtDesc(assignmentIds);
                var personalNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
                
                System.out.println("Found " + assignmentNotifications.size() + " assignment notifications");
                System.out.println("Found " + personalNotifications.size() + " personal notifications");
                
                // Combine both lists
                notifications = new java.util.ArrayList<>();
                notifications.addAll(assignmentNotifications);
                notifications.addAll(personalNotifications);
                
                // Sort by created date descending
                notifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));
                
                System.out.println("Total notifications for student: " + notifications.size());
            } else {
                // If no classroom, only get personal notifications
                notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            }
        } else {
            // For teachers, get submission notifications
            var assignmentIds = assignmentRepository.findByTeacherId(user.getId())
                                                    .stream().map(a -> a.getId())
                                                    .collect(Collectors.toList());

            notifications = notificationRepository.findByAssignmentIdInAndTeacherIsNullAndUserIsNotNullOrderByCreatedAtDesc(assignmentIds);
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
