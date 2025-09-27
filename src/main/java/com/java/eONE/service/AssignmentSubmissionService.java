package com.java.eONE.service;

import com.java.eONE.DTO.ViewSubmittedAssignmentDTO;
import com.java.eONE.model.AssignmentSubmission;

import java.util.List;
import java.util.Optional;

public interface AssignmentSubmissionService {
    AssignmentSubmission saveSubmission(AssignmentSubmission submission);
    Optional<AssignmentSubmission> getSubmissionById(Long id);
    AssignmentSubmission updateMarksAndGrade(Long id, Integer marks, String grade);
    List<AssignmentSubmission> findByUserId(Long userId);
    List<ViewSubmittedAssignmentDTO> getSubmissionsByAssignment(Long assignmentId);
    
    boolean submitMarks(Long submissionId, Integer marks, String grade);

}
