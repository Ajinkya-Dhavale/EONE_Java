package com.java.eONE.controller;

import com.java.eONE.enums.RoleType;
import com.java.eONE.enums.TeacherType;
import com.java.eONE.model.User;
import com.java.eONE.model.Classroom;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/teacher_roles")
public class TeacherRoleController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClassroomRepository classroomRepository;

    // Endpoint to set a teacher as class teacher for a classroom
    @PatchMapping("/{userId}/set_class_teacher")
    public ResponseEntity<?> setClassTeacher(@PathVariable Long userId, @RequestParam(required = false) Long classroomId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Check if user is a teacher
            if (!RoleType.TEACHER.getCode().equals(user.getRole().getName())) {
                return ResponseEntity.status(400).body(Map.of("error", "User is not a teacher"));
            }
            
            Classroom classroom = null;
            
            // If classroomId is provided, use it; otherwise, try to get from user's classroom
            if (classroomId != null && classroomId > 0) {
                classroom = classroomRepository.findById(classroomId).orElse(null);
                if (classroom == null) {
                    return ResponseEntity.status(404).body(Map.of("error", "Classroom not found with ID: " + classroomId));
                }
            } else {
                // Try to get classroom from user's classroom relationship
                if (user.getClassroom() != null) {
                    classroom = user.getClassroom();
                } else {
                    return ResponseEntity.status(400).body(Map.of("error", "Teacher must be assigned to a classroom first. Please assign the teacher to a classroom before setting them as class teacher."));
                }
            }
            
            // Check if classroom already has a class teacher
            User existingClassTeacher = classroom.getTeacher();
            if (existingClassTeacher != null && !existingClassTeacher.getId().equals(userId)) {
                // Remove existing class teacher role (set to subject teacher)
                existingClassTeacher.setTeacherType(com.java.eONE.enums.TeacherType.SUBJECT_TEACHER.getCode());
                userRepository.save(existingClassTeacher);
            }
            
            // Ensure user is assigned to this classroom
            if (user.getClassroom() == null || !user.getClassroom().getId().equals(classroom.getId())) {
                user.setClassroom(classroom);
            }
            
            // Set new class teacher
            user.setTeacherType(com.java.eONE.enums.TeacherType.CLASS_TEACHER.getCode());
            userRepository.save(user);
            
            // Update classroom to point to new class teacher
            classroom.setTeacher(user);
            classroomRepository.save(classroom);
            
            return ResponseEntity.ok(Map.of("message", "User set as class teacher successfully"));
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(500).body(Map.of("error", "Failed to set class teacher: " + e.getMessage()));
        }
    }

    // Endpoint to set a teacher as subject teacher for a classroom
    @PatchMapping("/{userId}/set_subject_teacher")
    public ResponseEntity<?> setSubjectTeacher(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Check if user is a teacher
            if (!RoleType.TEACHER.getCode().equals(user.getRole().getName())) {
                return ResponseEntity.status(400).body(Map.of("error", "User is not a teacher"));
            }
            
            // If user was a class teacher, remove them from classroom
            if (TeacherType.CLASS_TEACHER.getCode().equals(user.getTeacherType()) && user.getClassroom() != null) {
                Classroom classroom = user.getClassroom();
                if (classroom.getTeacher() != null && classroom.getTeacher().getId().equals(userId)) {
                    classroom.setTeacher(null);
                    classroomRepository.save(classroom);
                }
            }
            
            // Set teacher type to SUBJECT_TEACHER
            user.setTeacherType(TeacherType.SUBJECT_TEACHER.getCode());
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "User set as subject teacher successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to set subject teacher: " + e.getMessage()));
        }
    }

    // Endpoint to get teacher type
    @GetMapping("/{userId}/teacher_type")
    public ResponseEntity<?> getTeacherType(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            String teacherType = user.getTeacherType() != null ? user.getTeacherType() : TeacherType.SUBJECT_TEACHER.getCode();
            
            return ResponseEntity.ok(Map.of("teacher_type", teacherType));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get teacher type: " + e.getMessage()));
        }
    }
}