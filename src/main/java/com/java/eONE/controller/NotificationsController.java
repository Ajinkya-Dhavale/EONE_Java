package com.java.eONE.controller;

import com.java.eONE.DTO.NotificationMessageDTO;
import com.java.eONE.model.Notification;
import com.java.eONE.model.User;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.NotificationRepository;
import com.java.eONE.repository.SubjectRepository;
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
    
    @Autowired
    private SubjectRepository subjectRepository;

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

    // Test endpoint to create a sample assignment and verify student notifications
    @PostMapping("/test/assignment/{teacherId}/{subjectId}")
    public ResponseEntity<?> createTestAssignment(
            @PathVariable Long teacherId,
            @PathVariable Long subjectId) {
        try {
            var teacherOpt = userRepository.findById(teacherId);
            var subjectOpt = subjectRepository.findById(subjectId);
            
            if (teacherOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher not found"));
            }
            
            // Create a test assignment notification for students
            var teacher = teacherOpt.get();
            var classroom = teacher.getClassroom();
            
            if (classroom == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher has no classroom assigned"));
            }
            
            // Get all students in the classroom
            var students = userRepository.findByClassroomIdAndRoleName(classroom.getId(), "Student");
            
            int notificationCount = 0;
            for (User student : students) {
                Notification studentNotification = new Notification();
                studentNotification.setUser(student);
                studentNotification.setMessage("Test assignment created by " + teacher.getName() + 
                    ": Sample Test Assignment. Due date: 2024-12-31. Complete it before the due date!");
                studentNotification.setCreatedAt(java.time.LocalDateTime.now());
                studentNotification.setUpdatedAt(java.time.LocalDateTime.now());
                notificationRepository.save(studentNotification);
                notificationCount++;
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Test assignment notifications created",
                "teacherId", teacherId,
                "classroomId", classroom.getId(),
                "studentsCount", students.size(),
                "notificationsCreated", notificationCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to create sample notifications for a specific student
    @PostMapping("/test/student/{studentId}")
    public ResponseEntity<?> createTestStudentNotifications(@PathVariable Long studentId) {
        try {
            var studentOpt = userRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
            }
            
            var student = studentOpt.get();
            
            // Create assignment creation notification
            Notification assignmentNotification = new Notification();
            assignmentNotification.setUser(student);
            assignmentNotification.setMessage("New assignment created by Mr. Smith: Mathematics Assignment. Due date: 2024-12-15. Complete it before the due date!");
            assignmentNotification.setCreatedAt(java.time.LocalDateTime.now());
            assignmentNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(assignmentNotification);
            
            // Create grading notification
            Notification gradingNotification = new Notification();
            gradingNotification.setUser(student);
            gradingNotification.setMessage("Your assignment 'Mathematics Assignment' has been graded. Marks: 85, Grade: B+");
            gradingNotification.setCreatedAt(java.time.LocalDateTime.now());
            gradingNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(gradingNotification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test notifications created for student",
                "studentId", studentId,
                "studentName", student.getName(),
                "notificationsCreated", 2
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to verify notification filtering by role
    @GetMapping("/test/filtering/{userId}")
    public ResponseEntity<?> testNotificationFiltering(@PathVariable Long userId) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            var user = userOpt.get();
            var notifications = notificationService.getUserNotifications(userId, 10);
            
            // Get raw notifications for debugging
            var allNotifications = notificationRepository.findAll();
            var studentNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            var teacherNotifications = notificationRepository.findByTeacherIdOrderByCreatedAtDesc(userId);
            
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "userName", user.getName(),
                "userRole", user.getRole().getName(),
                "filteredNotifications", notifications.size(),
                "filteredNotificationMessages", notifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList()),
                "debugInfo", Map.of(
                    "totalNotificationsInDB", allNotifications.size(),
                    "studentNotificationsCount", studentNotifications.size(),
                    "teacherNotificationsCount", teacherNotifications.size(),
                    "studentNotificationMessages", studentNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList()),
                    "teacherNotificationMessages", teacherNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList())
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to create proper notifications for testing
    @PostMapping("/test/create-proper-notifications/{teacherId}/{studentId}")
    public ResponseEntity<?> createProperTestNotifications(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {
        try {
            var teacherOpt = userRepository.findById(teacherId);
            var studentOpt = userRepository.findById(studentId);
            
            if (teacherOpt.isEmpty() || studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher or student not found"));
            }
            
            var teacher = teacherOpt.get();
            var student = studentOpt.get();
            
            // Create teacher notification (should show in teacher dashboard)
            Notification teacherNotification = new Notification();
            teacherNotification.setTeacher(teacher);
            teacherNotification.setUser(null);
            teacherNotification.setMessage(student.getName() + " has completed an assignment: Test Assignment. Please review it.");
            teacherNotification.setCreatedAt(java.time.LocalDateTime.now());
            teacherNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(teacherNotification);
            
            // Create student notification (should show in student dashboard)
            Notification studentNotification = new Notification();
            studentNotification.setUser(student);
            studentNotification.setTeacher(null);
            studentNotification.setMessage("New assignment created by " + teacher.getName() + ": Mathematics Assignment. Due date: 2024-12-15. Complete it before the due date!");
            studentNotification.setCreatedAt(java.time.LocalDateTime.now());
            studentNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(studentNotification);
            
            // Create student grading notification (should show in student dashboard)
            Notification gradingNotification = new Notification();
            gradingNotification.setUser(student);
            gradingNotification.setTeacher(null);
            gradingNotification.setMessage("Your assignment 'Mathematics Assignment' has been graded. Marks: 85, Grade: B+");
            gradingNotification.setCreatedAt(java.time.LocalDateTime.now());
            gradingNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(gradingNotification);
            
            return ResponseEntity.ok(Map.of(
                "message", "Proper test notifications created",
                "teacherId", teacherId,
                "studentId", studentId,
                "teacherName", teacher.getName(),
                "studentName", student.getName(),
                "notificationsCreated", 3,
                "expectedTeacherNotifications", 1,
                "expectedStudentNotifications", 2
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Debug endpoint to check all notifications in database
    @GetMapping("/debug/all-notifications")
    public ResponseEntity<?> debugAllNotifications() {
        try {
            var allNotifications = notificationRepository.findAll();
            
            var notificationDetails = allNotifications.stream().map(n -> Map.of(
                "id", n.getId(),
                "message", n.getMessage(),
                "userId", n.getUser() != null ? n.getUser().getId() : null,
                "userName", n.getUser() != null ? n.getUser().getName() : null,
                "teacherId", n.getTeacher() != null ? n.getTeacher().getId() : null,
                "teacherName", n.getTeacher() != null ? n.getTeacher().getName() : null,
                "assignmentId", n.getAssignment() != null ? n.getAssignment().getId() : null,
                "createdAt", n.getCreatedAt()
            )).collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "totalNotifications", allNotifications.size(),
                "notifications", notificationDetails
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Cleanup endpoint to delete all existing notifications and create proper test data
    @PostMapping("/test/cleanup-and-setup/{teacherId}/{studentId}")
    public ResponseEntity<?> cleanupAndSetupTestNotifications(
            @PathVariable Long teacherId,
            @PathVariable Long studentId) {
        try {
            var teacherOpt = userRepository.findById(teacherId);
            var studentOpt = userRepository.findById(studentId);
            
            if (teacherOpt.isEmpty() || studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher or student not found"));
            }
            
            var teacher = teacherOpt.get();
            var student = studentOpt.get();
            
            // Delete all existing notifications
            notificationRepository.deleteAll();
            
            // Create proper teacher notification (should show in teacher dashboard)
            // This is when a student submits an assignment - teacher should be notified
            Notification teacherNotification = new Notification();
            teacherNotification.setTeacher(teacher);
            teacherNotification.setUser(null);
            teacherNotification.setMessage(student.getName() + " has completed an assignment: Mathematics Assignment. Please review it.");
            teacherNotification.setCreatedAt(java.time.LocalDateTime.now());
            teacherNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(teacherNotification);
            
            // Create proper student assignment notification (should show in student dashboard)
            // This is when a teacher creates an assignment - students should be notified
            Notification studentAssignmentNotification = new Notification();
            studentAssignmentNotification.setUser(student);
            studentAssignmentNotification.setTeacher(null);
            studentAssignmentNotification.setMessage("New assignment created by " + teacher.getName() + ": Mathematics Assignment. Due date: 2024-12-15. Complete it before the due date!");
            studentAssignmentNotification.setCreatedAt(java.time.LocalDateTime.now());
            studentAssignmentNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(studentAssignmentNotification);
            
            // Create proper student grading notification (should show in student dashboard)
            // This is when a teacher grades an assignment - student should be notified
            Notification studentGradingNotification = new Notification();
            studentGradingNotification.setUser(student);
            studentGradingNotification.setTeacher(null);
            studentGradingNotification.setMessage("Your assignment 'Mathematics Assignment' has been graded. Marks: 85, Grade: B+");
            studentGradingNotification.setCreatedAt(java.time.LocalDateTime.now());
            studentGradingNotification.setUpdatedAt(java.time.LocalDateTime.now());
            notificationRepository.save(studentGradingNotification);
            
            // Verify the setup
            var teacherNotifications = notificationRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
            var studentNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(studentId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Cleanup and setup completed - FIXED VERSION",
                "teacherId", teacherId,
                "studentId", studentId,
                "teacherName", teacher.getName(),
                "studentName", student.getName(),
                "teacherNotificationsCount", teacherNotifications.size(),
                "studentNotificationsCount", studentNotifications.size(),
                "teacherNotificationMessages", teacherNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList()),
                "studentNotificationMessages", studentNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList()),
                "expectedResults", Map.of(
                    "teacherShouldSee", "Student submission notifications",
                    "studentShouldSee", "Assignment creation and grading notifications"
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to verify frontend integration
    @GetMapping("/test/frontend-integration/{userId}")
    public ResponseEntity<?> testFrontendIntegration(@PathVariable Long userId) {
        try {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            var user = userOpt.get();
            var notifications = notificationService.getUserNotifications(userId, 10);
            
            // Get raw notifications for debugging
            var allNotifications = notificationRepository.findAll();
            var studentNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            var teacherNotifications = notificationRepository.findByTeacherIdOrderByCreatedAtDesc(userId);
            
            return ResponseEntity.ok(Map.of(
                "frontendTest", Map.of(
                    "userId", userId,
                    "userName", user.getName(),
                    "userRole", user.getRole().getName(),
                    "apiEndpoint", "/api/v1/notifications/" + userId,
                    "expectedForStudent", "Should see assignment creation and grading notifications",
                    "expectedForTeacher", "Should see student submission notifications"
                ),
                "backendData", Map.of(
                    "totalNotificationsInDB", allNotifications.size(),
                    "studentNotificationsCount", studentNotifications.size(),
                    "teacherNotificationsCount", teacherNotifications.size(),
                    "filteredNotificationsForUser", notifications.size(),
                    "filteredNotificationMessages", notifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList())
                ),
                "rawData", Map.of(
                    "studentNotificationMessages", studentNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList()),
                    "teacherNotificationMessages", teacherNotifications.stream().map(n -> n.getMessage()).collect(java.util.stream.Collectors.toList())
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}

