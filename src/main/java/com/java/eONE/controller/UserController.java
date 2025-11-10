package com.java.eONE.controller;

import com.java.eONE.DTO.TeacherDashboardCountDTO;
import com.java.eONE.DTO.UserResponseDTO;
import com.java.eONE.enums.RoleType;
import com.java.eONE.enums.TeacherType;
import com.java.eONE.enums.UserStatus;
import com.java.eONE.model.Classroom;
import com.java.eONE.model.Role;
import com.java.eONE.model.User;
import com.java.eONE.model.Subject;
import com.java.eONE.model.Assignment;
import com.java.eONE.model.AssignmentSubmission;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.repository.RoleRepository;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.AssignmentSubmissionRepository;
import com.java.eONE.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private ClassroomRepository classroomRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private AssignmentSubmissionRepository assignmentSubmissionRepository;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user,
                                      @RequestParam(required = false) Long roleId,
                                      @RequestParam(required = false) Long classroomId,
                                      @RequestParam(required = false) String teacherType) {

        if (roleId != null) {
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        if (classroomId != null) {
            Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));
            user.setClassroom(classroom);
        }

        // Set teacher type if provided and user is a teacher
        if (teacherType != null && RoleType.TEACHER.getCode().equals(user.getRole().getName())) {
            user.setTeacherType(teacherType);
        } else if (RoleType.TEACHER.getCode().equals(user.getRole().getName())) {
            // Default to SUBJECT_TEACHER if not specified
            user.setTeacherType(TeacherType.SUBJECT_TEACHER.getCode());
        }

        UserResponseDTO userDTO = userService.registerUser(user);
        return ResponseEntity.status(201).body(Map.of(
            "message", "User registered successfully. Once the request is approved, you can log in.",
            "user", userDTO
        ));
    }


    @GetMapping("/pending_approvals")
    public ResponseEntity<?> getPendingApprovals(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getPendingApprovals(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/approved_users")
    public ResponseEntity<?> getApprovedUsers(@RequestParam String type, @RequestParam(required = false) Long teacherId) {
        List<UserResponseDTO> users = userService.getApprovedUsers(type, teacherId);
        return ResponseEntity.ok(Map.of("users", users));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        try {
            // Approve user in service
            userService.approveUser(id);

            // Fetch updated user details
            UserResponseDTO updatedUser = userService.getUserById(id);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser); // ✅ Return full user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        try {
            // Reject user in service
            userService.rejectUser(id);

            // Fetch updated user details
            UserResponseDTO updatedUser = userService.getUserById(id);

            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser); // ✅ Return full user object
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserResponseDTO userDTO = userService.getUserById(id);
        if (userDTO == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Invalid user id, user not found"));
        }
        return ResponseEntity.ok(Map.of("user", userDTO));
    }

    @GetMapping("/admin_dashboard_count")
    public ResponseEntity<?> adminDashboardCount() {
        long pendingApprovals = userService.getPendingApprovalsCount();
        long classroomCount = userService.getClassroomCount();
        return ResponseEntity.ok(Map.of(
                "pending_approvals_count", pendingApprovals,
                "classroom_count", classroomCount
        ));
    }

    @GetMapping("/{teacherId}/teacher_dashboard_count")
    public ResponseEntity<?> teacherDashboardCount(@PathVariable Long teacherId) {
        TeacherDashboardCountDTO countDto = userService.getTeacherDashboardCount(teacherId);
        return ResponseEntity.ok(countDto);
    }

    // Student dashboard overview counts
    @GetMapping("/student_dashboard_count")
    public ResponseEntity<?> studentDashboardCount(@RequestParam("student_id") Long studentId) {
        var userOpt = userRepository.findById(studentId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
        }
        var student = userOpt.get();
        var classroom = student.getClassroom();
        if (classroom == null) {
            return ResponseEntity.ok(Map.of(
                "total_subjects", 0,
                "assignments_due", 0,
                "pending_submissions", 0
            ));
        }

        List<Subject> subjects = subjectRepository.findByClassroomId(classroom.getId());
        int totalSubjects = subjects.size();
        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();

        List<Assignment> assignments = subjectIds.isEmpty() ? java.util.List.of() : assignmentRepository.findBySubjectIdIn(subjectIds);

        // assignments due = assignments with due_date >= today
        java.time.LocalDate today = java.time.LocalDate.now();
        int assignmentsDue = (int) assignments.stream()
            .filter(a -> a.getDueDate() != null && !a.getDueDate().isBefore(today))
            .count();

        // pending submissions = assignments without a submission by this student
        List<Long> assignmentIds = assignments.stream().map(Assignment::getId).toList();
        List<AssignmentSubmission> mySubs = assignmentIds.isEmpty() ? java.util.List.of() : assignmentSubmissionRepository.findByUserId(studentId);
        java.util.Set<Long> submittedAssignmentIds = mySubs.stream().map(s -> s.getAssignment().getId()).collect(java.util.stream.Collectors.toSet());
        int pendingSubmissions = (int) assignmentIds.stream().filter(id -> !submittedAssignmentIds.contains(id)).count();

        return ResponseEntity.ok(Map.of(
            "total_subjects", totalSubjects,
            "assignments_due", assignmentsDue,
            "pending_submissions", pendingSubmissions
        ));
    }

    @PatchMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(name = "mobile_number", required = false) String mobileNumber,
            @RequestParam(name = "date_of_birth", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateOfBirth
    ) {
        var dto = userService.updateProfile(id, name, mobileNumber, dateOfBirth);
        if (dto == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        return ResponseEntity.ok(Map.of("user", dto));
    }

    @PostMapping("/{id}/change_password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String currentPassword = body.get("current_password");
        String newPassword = body.get("new_password");
        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.status(400).body(Map.of("error", "Missing current_password or new_password"));
        }
        boolean ok = userService.changePassword(id, currentPassword, newPassword);
        if (!ok) return ResponseEntity.status(400).body(Map.of("error", "Current password incorrect or user not found"));
        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }

    @PostMapping(value = "/{id}/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "No file provided"));
        }
        String uploadDir = "uploads";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "File upload failed"));
        }
        var dto = userService.setAvatarFilename(id, fileName);
        if (dto == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        return ResponseEntity.ok(Map.of("user", dto));
    }

    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<?> removeAvatar(@PathVariable Long id) {
        var dto = userService.removeAvatar(id);
        if (dto == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        return ResponseEntity.ok(Map.of("user", dto));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            boolean blocked = userService.blockUser(id);
            if (blocked) {
                UserResponseDTO updatedUser = userService.getUserById(id);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        try {
            boolean unblocked = userService.unblockUser(id);
            if (unblocked) {
                UserResponseDTO updatedUser = userService.getUserById(id);
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/delete_user")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Check if teacher has subjects or assignments
            if (user.getRole() != null && RoleType.TEACHER.getCode().equals(user.getRole().getName())) {
                boolean canDelete = userService.canDeleteTeacher(id);
                if (!canDelete && (user.getStatus() == null || user.getStatus() != UserStatus.BLOCKED.getValue())) {
                    return ResponseEntity.status(400).body(Map.of(
                        "error", "Cannot delete teacher with subjects or assignments. Please block the teacher first.",
                        "requires_block", true
                    ));
                }
            }
            
            boolean deleted = userService.deleteUserById(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "Failed to delete user"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", e.getMessage()));
        }
    }
}