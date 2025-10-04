package com.java.eONE.controller;

import com.java.eONE.DTO.NotificationMessageDTO;
import com.java.eONE.model.Notification;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.NotificationRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private final NotificationService notificationService;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AssignmentRepository assignmentRepository;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserNotifications(
            @PathVariable Long id,
            @RequestParam(required = false) Integer limit) {

        List<NotificationMessageDTO> notifications = notificationService.getUserNotifications(id, limit);

        if (notifications == null) {
            notifications = List.of(); // return empty list instead of null
        }

        return ResponseEntity.ok(notifications);
    }

    // Test endpoint to verify notification creation
    @GetMapping("/test/{userId}")
    public ResponseEntity<?> testNotifications(@PathVariable Long userId) {
        List<NotificationMessageDTO> notifications = notificationService.getUserNotifications(userId, null);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "notificationCount", notifications.size(),
            "notifications", notifications
        ));
    }

    // Create test notifications for a student
    @PostMapping("/test/create/{studentId}")
    public ResponseEntity<?> createTestNotifications(@PathVariable Long studentId) {
        try {
            var userOpt = userRepository.findById(studentId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
            }
            
            var student = userOpt.get();
            
            // Create a test assignment notification (teacher uploaded assignment)
            Notification assignmentNotification = new Notification();
            assignmentNotification.setTeacher(student); // Using student as teacher for test
            assignmentNotification.setMessage("Test Teacher has submitted a new assignment: Sample Assignment");
            assignmentNotification.setCreatedAt(java.time.LocalDateTime.now());
            assignmentNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(assignmentNotification);
            
            // Create a test grading notification (personal notification)
            Notification gradingNotification = new Notification();
            gradingNotification.setUser(student);
            gradingNotification.setMessage("Your assignment 'Sample Assignment' has been graded. Marks: 85, Grade: A");
            gradingNotification.setCreatedAt(java.time.LocalDateTime.now());
            gradingNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(gradingNotification);
            
            return ResponseEntity.ok(Map.of("message", "Test notifications created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Debug endpoint to check notification data
    @GetMapping("/debug/{userId}")
    public ResponseEntity<?> debugNotifications(@PathVariable Long userId) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            var user = userOpt.get();
            var classroom = user.getClassroom();
            
            if (classroom == null) {
                return ResponseEntity.ok(Map.of(
                    "error", "User has no classroom assigned",
                    "userId", userId,
                    "userName", user.getName()
                ));
            }
            
            var subjectIds = classroom.getSubjects().stream()
                                     .map(s -> s.getId())
                                     .collect(java.util.stream.Collectors.toList());
            
            var assignmentIds = assignmentRepository.findBySubjectIdIn(subjectIds)
                                                    .stream().map(a -> a.getId())
                                                    .collect(java.util.stream.Collectors.toList());
            
            // Get all notifications for debugging
            var allNotifications = notificationRepository.findAll();
            var assignmentNotifications = notificationRepository.findByAssignmentIdInAndUserIsNullAndTeacherIsNotNullOrderByCreatedAtDesc(assignmentIds);
            var personalNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "userName", user.getName(),
                "classroomId", classroom.getId(),
                "subjectIds", subjectIds,
                "assignmentIds", assignmentIds,
                "totalNotifications", allNotifications.size(),
                "assignmentNotifications", assignmentNotifications.size(),
                "personalNotifications", personalNotifications.size(),
                "allNotificationsData", allNotifications.stream().limit(5).map(n -> Map.of(
                    "id", n.getId(),
                    "message", n.getMessage(),
                    "teacherId", n.getTeacher() != null ? n.getTeacher().getId() : null,
                    "userId", n.getUser() != null ? n.getUser().getId() : null,
                    "assignmentId", n.getAssignment() != null ? n.getAssignment().getId() : null
                )).collect(java.util.stream.Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

