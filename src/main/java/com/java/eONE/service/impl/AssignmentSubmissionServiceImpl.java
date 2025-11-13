package com.java.eONE.service.impl;

import com.java.eONE.DTO.ViewSubmittedAssignmentDTO;
import com.java.eONE.model.AssignmentSubmission;
import com.java.eONE.repository.AssignmentSubmissionRepository;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;

    public AssignmentSubmissionServiceImpl(AssignmentSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }
    
    @Override
    public List<AssignmentSubmission> findByUserId(Long userId) {
        return submissionRepository.findByUserId(userId);
    }

    @Override
    public List<AssignmentSubmission> findByAssignmentIdAndUserId(Long assignmentId, Long userId) {
        return submissionRepository.findByAssignmentIdAndUserId(assignmentId, userId);
    }

    @Override
    public AssignmentSubmission saveSubmission(AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Optional<AssignmentSubmission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    @Override
    public AssignmentSubmission updateMarksAndGrade(Long id, Integer marks, String grade) {
        Optional<AssignmentSubmission> optionalSubmission = submissionRepository.findById(id);
        if (optionalSubmission.isPresent()) {
            AssignmentSubmission submission = optionalSubmission.get();
            submission.setMarks(marks);
            // Auto-calculate grade if marks are provided
            if (marks != null && submission.getAssignment().getTotalMarks() != null && submission.getAssignment().getTotalMarks() > 0) {
                String calculatedGrade = calculateGrade(marks, submission.getAssignment().getTotalMarks());
                submission.setGrade(calculatedGrade);
            } else if (grade != null) {
                submission.setGrade(grade);
            }
            submission.setStatus("graded");
            return submissionRepository.save(submission);
        } else {
            throw new RuntimeException("AssignmentSubmission not found with id " + id);
        }
    }

    /**
     * Calculate grade based on percentage of marks obtained
     * Grade scale:
     * - 90-100%: O (Outstanding)
     * - 80-89%: A (Excellent)
     * - 70-79%: B (Good)
     * - 60-69%: C (Satisfactory)
     * - 50-59%: D (Pass)
     * - Below 50%: F (Fail)
     */
    private String calculateGrade(Integer marks, Integer totalMarks) {
        if (marks == null || totalMarks == null || totalMarks == 0) {
            return null;
        }
        
        double percentage = (marks.doubleValue() / totalMarks.doubleValue()) * 100;
        
        if (percentage >= 90) {
            return "O";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B";
        } else if (percentage >= 60) {
            return "C";
        } else if (percentage >= 50) {
            return "D";
        } else {
            return "F";
        }
    }
    
    @Override
    public List<ViewSubmittedAssignmentDTO> getSubmissionsByAssignment(Long assignmentId) {
        List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        return submissions.stream().map(s -> {
            ViewSubmittedAssignmentDTO dto = new ViewSubmittedAssignmentDTO();
            dto.setId(s.getId());
            dto.setAssignmentId(s.getAssignment().getId());
            dto.setUserId(s.getUser().getId());
            dto.setStudentName(s.getUser().getName());
            dto.setFile(s.getFile());
            
            // Generate proper file URL similar to how teacher assignments work
            String fileUrl = null;
            if (s.getFile() != null && !s.getFile().isEmpty()) {
                fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/submissionFile/")
                        .path(s.getFile())
                        .toUriString();
            }
            dto.setFileUrl(fileUrl);
            
            dto.setCreatedAt(s.getCreatedAt());
            dto.setMarks(s.getMarks());
            dto.setGrade(s.getGrade());
            dto.setReview(s.getReview());
            // Get total marks from assignment (set when teacher created the assignment)
            if (s.getAssignment() != null) {
                dto.setTotalMarks(s.getAssignment().getTotalMarks());
            } else {
                dto.setTotalMarks(null);
            }
            // Set status based on submission state
            if (s.getStatus() != null && !s.getStatus().isEmpty()) {
                dto.setStatus(s.getStatus());
            } else if (s.getMarks() != null) {
                dto.setStatus("graded");
            } else if (s.getReview() != null && !s.getReview().isEmpty()) {
                dto.setStatus("reviewed");
            } else {
                dto.setStatus("pending");
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public boolean submitMarks(Long submissionId, Integer marks, String grade) {
        var optSubmission = submissionRepository.findById(submissionId);
        if (optSubmission.isEmpty()) return false;

        AssignmentSubmission submission = optSubmission.get();
        
        // Validate marks don't exceed total marks
        if (marks != null && submission.getAssignment().getTotalMarks() != null) {
            if (marks > submission.getAssignment().getTotalMarks()) {
                throw new IllegalArgumentException("Marks cannot exceed total marks (" + submission.getAssignment().getTotalMarks() + ")");
            }
        }
        
        submission.setMarks(marks);
        // Auto-calculate grade if marks are provided
        if (marks != null && submission.getAssignment().getTotalMarks() != null && submission.getAssignment().getTotalMarks() > 0) {
            String calculatedGrade = calculateGrade(marks, submission.getAssignment().getTotalMarks());
            submission.setGrade(calculatedGrade);
        } else if (grade != null) {
            submission.setGrade(grade);
        }
        submission.setStatus("graded");
        submissionRepository.save(submission);
        return true;
    }

    @Override
    public boolean hasAnySubmission(Long assignmentId) {
        return !submissionRepository.findByAssignmentId(assignmentId).isEmpty();
    }

    @Override
    public boolean hasAnyGradedSubmission(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId)
                .stream().anyMatch(s -> s.getMarks() != null || (s.getGrade() != null && !s.getGrade().isEmpty()));
    }

    @Override
    public AssignmentSubmission submitReview(Long id, String review) {
        Optional<AssignmentSubmission> optionalSubmission = submissionRepository.findById(id);
        if (optionalSubmission.isPresent()) {
            AssignmentSubmission submission = optionalSubmission.get();
            submission.setReview(review);
            submission.setStatus("reviewed");
            // Clear marks and grade when submitting review
            submission.setMarks(null);
            submission.setGrade(null);
            return submissionRepository.save(submission);
        } else {
            throw new RuntimeException("AssignmentSubmission not found with id " + id);
        }
    }
}
