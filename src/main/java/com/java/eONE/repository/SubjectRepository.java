package com.java.eONE.repository;

import com.java.eONE.model.Subject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	   List<Subject> findByTeacherId(Long teacherId);
	   long countByTeacherId(Long teacherId);
}
