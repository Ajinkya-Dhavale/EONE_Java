package com.java.eONE.controller;

import com.java.eONE.DTO.AssignmentSubmissionRequestDTO;
import com.java.eONE.DTO.AssignmentSubmissionResponseDTO;
import com.java.eONE.model.*;
import com.java.eONE.repository.*;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

        // Use your existing folder
        String uploadDir = System.getProperty("user.dir") + File.separator + "submissionFile";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            // Make sure folder exists
            Files.createDirectories(Paths.get(uploadDir));

            // Save the uploaded file
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace(); // See exact cause
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save file"));
        }

        // Save submission record
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFile(fileName); // save filename only
        submission.setCreatedAt(LocalDateTime.now());
        submission.setUpdatedAt(LocalDateTime.now());

        AssignmentSubmission savedSubmission = submissionService.saveSubmission(submission);

        // Notify teacher
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setAssignment(assignment);
        notification.setMessage(user.getName() + " has completed an assignment: " + assignment.getTitle() + ". Please review it.");
        notification.setCreatedAt(LocalDateTime.now());  // ✅ add this
        notification.setUpdatedAt(LocalDateTime.now());  // ✅ add this
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

//        responseDTO.setFileUrl("http://192.168.1.33:8080/submissionFile/" + savedSubmission.getFile());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Submission successful", "submission", responseDTO));
    }




    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMarksAndGrade(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        Integer marks = (Integer) payload.get("marks");
        String grade = (String) payload.get("grade");

        AssignmentSubmission updatedSubmission = submissionService.updateMarksAndGrade(id, marks, grade);
        return ResponseEntity.ok(Map.of("message", "Marks updated successfully", "submission", updatedSubmission));
    }

    
    @GetMapping
    public ResponseEntity<?> getSubmissionsByStudent(@RequestParam("student_id") Long studentId) {
        Optional<User> studentOpt = userRepository.findById(studentId);
        if(studentOpt.isEmpty()) {
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
                    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/submissionFile/")
                            .path(sub.getFile())
                            .toUriString();
                    dto.setFileUrl(url);
                    return dto;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


}
