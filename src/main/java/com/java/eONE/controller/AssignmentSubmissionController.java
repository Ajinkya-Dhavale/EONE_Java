package com.java.eONE.controller;

import com.java.eONE.DTO.AssignmentSubmissionResponseDTO;
import com.java.eONE.model.*;
import com.java.eONE.repository.*;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/assignment_submissions")
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService submissionService;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AssignmentSubmissionController(AssignmentSubmissionService submissionService,
                                          AssignmentRepository assignmentRepository,
                                          UserRepository userRepository,
                                          NotificationRepository notificationRepository) {
        this.submissionService = submissionService;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // ---------------- Create submission ----------------
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSubmission(
            @RequestParam("assignment_submission[assignment_id]") Long assignmentId,
            @RequestParam("assignment_submission[user_id]") Long userId,
            @RequestParam("assignment_submission[file]") MultipartFile file) {

        var assignment = assignmentRepository.findById(assignmentId).orElse(null);
        var user = userRepository.findById(userId).orElse(null);

        if (assignment == null || user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid assignment_id or user_id"));
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "submissionFile";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(Paths.get(uploadDir));
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save file"));
        }

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFile(fileName);
        submission.setCreatedAt(LocalDateTime.now());
        submission.setUpdatedAt(LocalDateTime.now());

        AssignmentSubmission savedSubmission = submissionService.saveSubmission(submission);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setAssignment(assignment);
        notification.setMessage(user.getName() + " has completed an assignment: " + assignment.getTitle() + ". Please review it.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        AssignmentSubmissionResponseDTO responseDTO = new AssignmentSubmissionResponseDTO();
        responseDTO.setId(savedSubmission.getId());
        responseDTO.setAssignmentId(savedSubmission.getAssignment().getId());
        responseDTO.setUserId(savedSubmission.getUser().getId());
        responseDTO.setFile(savedSubmission.getFile());
        responseDTO.setCreatedAt(savedSubmission.getCreatedAt());
        responseDTO.setUpdatedAt(savedSubmission.getUpdatedAt());
        responseDTO.setMarks(savedSubmission.getMarks());
        responseDTO.setGrade(savedSubmission.getGrade());
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/submissionFile/")
                .path(savedSubmission.getFile())
                .toUriString();
        responseDTO.setFileUrl(fileUrl);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Submission successful", "submission", responseDTO));
    }

    // ---------------- Teacher grading ----------------
    @PatchMapping("/grade/{id}")
    public ResponseEntity<?> updateMarksAndGrade(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        Integer marks = (payload.get("marks") instanceof Number) ? ((Number) payload.get("marks")).intValue() : null;
        String grade = (String) payload.get("grade");
        AssignmentSubmission updatedSubmission = submissionService.updateMarksAndGrade(id, marks, grade);
        return ResponseEntity.ok(Map.of("message", "Marks updated successfully", "submission", updatedSubmission));
    }

    // ---------------- Student submissions list ----------------
    @GetMapping
    public ResponseEntity<?> getSubmissionsByStudent(@RequestParam("student_id") Long studentId) {
        Optional<User> studentOpt = userRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid student_id"));
        }

        List<AssignmentSubmission> submissions = submissionService.findByUserId(studentId);
        List<AssignmentSubmissionResponseDTO> dtos = submissions.stream()
                .map(sub -> {
                    AssignmentSubmissionResponseDTO dto = new AssignmentSubmissionResponseDTO();
                    dto.setId(sub.getId());
                    dto.setAssignmentId(sub.getAssignment().getId());
                    dto.setUserId(sub.getUser().getId());
                    dto.setFile(sub.getFile());
                    dto.setCreatedAt(sub.getCreatedAt());
                    dto.setUpdatedAt(sub.getUpdatedAt());
                    dto.setMarks(sub.getMarks());
                    dto.setGrade(sub.getGrade());
                    
                    // Add assignment title
                    dto.setAssignmentTitle(sub.getAssignment().getTitle());
                    
                    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/submissionFile/")
                            .path(sub.getFile())
                            .toUriString();
                    dto.setFileUrl(url);
                    return dto;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ---------------- Student update file before grade/due ----------------
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSubmissionFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        var opt = submissionService.getSubmissionById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Submission not found"));
        }
        var submission = opt.get();

        if (submission.getMarks() != null || (submission.getGrade() != null && !submission.getGrade().isEmpty())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Submission already graded"));
        }

        var assignment = submission.getAssignment();
        if (assignment.getDueDate() != null && assignment.getDueDate().isBefore(java.time.LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Past due date"));
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "submissionFile";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.createDirectories(Paths.get(uploadDir));
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save file"));
        }

        submission.setFile(fileName);
        submission.setUpdatedAt(LocalDateTime.now());
        submissionService.saveSubmission(submission);

        return ResponseEntity.ok(Map.of("message", "Submission updated"));
    }

}
