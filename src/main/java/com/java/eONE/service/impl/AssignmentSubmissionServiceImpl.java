package com.java.eONE.service.impl;

import com.java.eONE.DTO.ViewSubmittedAssignmentDTO;
import com.java.eONE.model.AssignmentSubmission;
import com.java.eONE.repository.AssignmentSubmissionRepository;
import com.java.eONE.service.AssignmentSubmissionService;
import org.springframework.stereotype.Service;

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
            submission.setGrade(grade);
            return submissionRepository.save(submission);
        } else {
            throw new RuntimeException("AssignmentSubmission not found with id " + id);
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
            dto.setFileUrl(s.getFileUrl());
            dto.setCreatedAt(s.getCreatedAt());
            dto.setMarks(s.getMarks());
            dto.setGrade(s.getGrade());
            dto.setStatus(s.getMarks() == null ? "pending" : "graded");
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public boolean submitMarks(Long submissionId, Integer marks, String grade) {
        var optSubmission = submissionRepository.findById(submissionId);
        if (optSubmission.isEmpty()) return false;

        AssignmentSubmission submission = optSubmission.get();
        submission.setMarks(marks);
        submission.setGrade(grade);
        submissionRepository.save(submission);
        return true;
    }
}
