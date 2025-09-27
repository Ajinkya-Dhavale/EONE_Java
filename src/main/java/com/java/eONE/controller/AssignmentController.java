package com.java.eONE.controller;

import com.java.eONE.DTO.AssignmentRequestDTO;
import com.java.eONE.DTO.AssignmentResponseDTO;
import com.java.eONE.DTO.ViewSubmittedAssignmentDTO;
import com.java.eONE.model.Assignment;
import com.java.eONE.model.Notification;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.AssignmentRepository;
import com.java.eONE.repository.NotificationRepository;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.AssignmentSubmissionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.*;
import java.util.stream.Collectors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/assignments")

public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final NotificationRepository notificationRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public AssignmentController(AssignmentRepository assignmentRepository,
                                NotificationRepository notificationRepository,
                                SubjectRepository subjectRepository,
                                UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.notificationRepository = notificationRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAssignment(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("due_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
            @RequestParam("subject_id") Long subjectId,
            @RequestParam("teacher_id") Long teacherId,
            @RequestParam("file") MultipartFile file) {

        // Validate related entities
        Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
        Optional<User> teacherOpt = userRepository.findById(teacherId);

        if (subjectOpt.isEmpty() || teacherOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid subject_id or teacher_id"));
        }

        // Save file to uploads folder inside project
        String uploadDir = "uploads"; // relative to project root
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed"));
        }

        // Save assignment in DB
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);
        assignment.setFile(fileName); // store only filename
        assignment.setSubject(subjectOpt.get());
        assignment.setTeacher(teacherOpt.get());
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        Assignment saved = assignmentRepository.save(assignment);

        // Create notification
        Notification notification = new Notification();
        notification.setTeacher(teacherOpt.get());
        notification.setAssignment(saved);
        notification.setMessage(teacherOpt.get().getName() + " has submitted a new assignment: " + saved.getTitle());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Assignment created successfully", "assignment", saved));
    }


    @GetMapping
    public ResponseEntity<?> getAssignments(
            @RequestParam(required = false) Long teacher_id,
            @RequestParam(required = false) Long student_id) {

        List<Assignment> assignments;

        if (teacher_id != null) {
            assignments = assignmentRepository.findByTeacherId(teacher_id);
        } 
        else if (student_id != null) {
            Optional<User> studentOpt = userRepository.findById(student_id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid student_id"));
            }

            var classroom = studentOpt.get().getClassroom();
            if (classroom == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Long> subjectIds = classroom.getSubjects().stream()
                    .map(sub -> sub.getId())
                    .toList();
            assignments = assignmentRepository.findBySubjectIdIn(subjectIds);
        } 
        else {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "teacher_id or student_id parameter required"));
        }

        // Convert Assignment -> Map<String, Object> for Flutter
        List<Map<String, Object>> dtos = assignments.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("title", a.getTitle());
            map.put("description", a.getDescription());
            map.put("due_date", a.getDueDate() != null ? a.getDueDate().toString() : null);
            map.put("subject_id", a.getSubject() != null ? a.getSubject().getId() : null);
            map.put("subject_name", a.getSubject() != null ? a.getSubject().getName() : null);
            map.put("teacher_id", a.getTeacher() != null ? a.getTeacher().getId() : null);
            map.put("teacher_name", a.getTeacher() != null ? a.getTeacher().getName() : null);
            String fileUrl = null;
            if (a.getFile() != null && !a.getFile().isEmpty()) {
                fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(a.getFile())
                        .toUriString();
            }
            map.put("file_url", fileUrl);
            return map;
        }).toList();

        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable Long id) {
        List<ViewSubmittedAssignmentDTO> submissions = submissionService.getSubmissionsByAssignment(id);

        if (submissions.isEmpty()) {
            return ResponseEntity.ok().body(List.of()); // Return empty list instead of 400
        }
        return ResponseEntity.ok(submissions);
    }
  
    @PatchMapping("/{submissionId}")
    public ResponseEntity<?> submitMarks(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> payload
    ) {
        Integer marks = (Integer) payload.get("marks");
        String grade = (String) payload.get("grade");
        boolean success = submissionService.submitMarks(submissionId, marks, grade);

        if(success) {
            return ResponseEntity.ok(Map.of("message", "Marks submitted successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Submission not found"));
        }
    }

    @Autowired
    private AssignmentSubmissionService submissionService;
    
    private AssignmentResponseDTO mapToResponseDTO(Assignment assignment) {
        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate());
        dto.setFile(assignment.getFile());
        dto.setSubjectId(assignment.getSubject().getId());
        dto.setTeacherId(assignment.getTeacher() != null ? assignment.getTeacher().getId() : null);
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(assignment.getFile())
                .toUriString();
        dto.setFileUrl(url);
        return dto;
    }
}
