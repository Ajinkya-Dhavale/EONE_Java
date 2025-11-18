package com.java.eONE.repository;

import com.java.eONE.model.AssignmentSubmission;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
	List<AssignmentSubmission> findByUserId(Long userId);
	
	  @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId ORDER BY s.createdAt DESC")
	    List<AssignmentSubmission> findSubmissionsByAssignmentId(@Param("assignmentId") Long assignmentId);
	  
	  @Query("SELECT s FROM AssignmentSubmission s JOIN FETCH s.assignment WHERE s.assignment.id = :assignmentId ORDER BY s.createdAt DESC")
	  List<AssignmentSubmission> findByAssignmentId(@Param("assignmentId") Long assignmentId);
	  
	  @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.user.id = :userId ORDER BY s.createdAt DESC")
	  List<AssignmentSubmission> findByAssignmentIdAndUserId(@Param("assignmentId") Long assignmentId, @Param("userId") Long userId);

}
