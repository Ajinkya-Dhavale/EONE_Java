package com.java.eONE.controller;

import com.java.eONE.DTO.SubjectDTO;
import com.java.eONE.model.Classroom;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/subjects")
public class SubjectsController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired private ClassroomRepository classroomRepository;

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Map<String, Object> body) {
        try {
            // Validate required fields
            String name = (String) body.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject name is required"));
            }
            
            // Get and validate teacher_id
            Object teacherIdObj = body.get("teacher_id");
            if (teacherIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher ID is required"));
            }
            Long teacherId;
            try {
                if (teacherIdObj instanceof Number) {
                    teacherId = ((Number) teacherIdObj).longValue();
                } else {
                    teacherId = Long.valueOf(teacherIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid teacher ID format"));
            }
            
            // Get and validate classroom_id
            Object classroomIdObj = body.get("classroom_id");
            if (classroomIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Classroom ID is required"));
            }
            Long classroomId;
            try {
                if (classroomIdObj instanceof Number) {
                    classroomId = ((Number) classroomIdObj).longValue();
                } else {
                    classroomId = Long.valueOf(classroomIdObj.toString());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid classroom ID format"));
            }

            // Get optional fields
            List<String> daysList = (List<String>) body.get("days_list");
            String startTime = (String) body.get("start_time");
            String endTime = (String) body.get("end_time");

            // Find teacher and classroom
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new RuntimeException("Classroom not found"));

            // Create subject
            Subject subject = new Subject();
            subject.setName(name);
            subject.setDaysList(daysList != null ? daysList.toArray(new String[0]) : new String[0]);
            subject.setStartTime(startTime);
            subject.setEndTime(endTime);
            subject.setTeacher(teacher);
            subject.setClassroom(classroom);
            subject.setCreatedAt(LocalDateTime.now());
            subject.setUpdatedAt(LocalDateTime.now());

            SubjectDTO savedDto = subjectService.createSubject(subject);

            return ResponseEntity.status(201).body(
                    Map.of("message", "Subject created successfully", "subject", savedDto)
            );
        } catch (IllegalArgumentException e) {
            // Handle duplicate subject name error
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create subject: " + e.getMessage()));
        }
    }



    @GetMapping("/{id}/subjects")
    public ResponseEntity<?> getSubjectsByTeacher(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        List<SubjectDTO> subjectsDto = subjectService.getSubjectsByTeacherId(user.getId());
        return ResponseEntity.ok(subjectsDto);
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<?> getSubjectsByClassroom(@PathVariable Long classroomId) {
        try {
            List<SubjectDTO> subjectsDto = subjectService.getSubjectsByClassroomId(classroomId);
            return ResponseEntity.ok(subjectsDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch subjects: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getSubjectsByStudent(@PathVariable Long studentId) {
        try {
            User student = userRepository.findById(studentId).orElse(null);
            if (student == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
            }
            
            if (student.getClassroom() == null) {
                return ResponseEntity.ok(List.of()); // Return empty list if no classroom
            }
            
            List<SubjectDTO> subjectsDto = subjectService.getSubjectsByClassroomId(student.getClassroom().getId());
            return ResponseEntity.ok(subjectsDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch subjects: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            boolean deleted = subjectService.deleteSubject(id);
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Subject deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "Subject not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete subject: " + e.getMessage()));
        }
    }

}