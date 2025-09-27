package com.java.eONE.repository;

import com.java.eONE.model.Assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
	
	List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findBySubjectIdIn(List<Long> subjectIds);

    // You need this to fetch submissions for an assignment (adjust accordingly)
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId")
    List<?> findSubmissionsByAssignmentId(Long assignmentId);
    
    long countByTeacherId(Long teacherId);
   
}
