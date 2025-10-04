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

    private static final String STUDENT_ROLE_NAME = "Student";

    public List<NotificationMessageDTO> getUserNotifications(Long userId, Integer limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            System.out.println("User not found with ID: " + userId);
            return List.of(); // return empty list instead of null
        }

        System.out.println("Getting notifications for user: " + user.getName() + " (Role: " + user.getRole().getName() + ")");
        List<Notification> notifications;

        if (STUDENT_ROLE_NAME.equals(user.getRole().getName())) {
            // For students, get ONLY their personal notifications (where user_id = studentId)
            // This includes assignment creation notifications and grading notifications
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            System.out.println("Found " + notifications.size() + " personal notifications for student: " + user.getName());
        } else {
            // For teachers, get ONLY teacher notifications (where teacher_id = teacherId)
            // This includes student submission notifications
            notifications = notificationRepository.findByTeacherIdOrderByCreatedAtDesc(userId);
            
            System.out.println("Found " + notifications.size() + " teacher notifications for teacher: " + user.getName());
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
