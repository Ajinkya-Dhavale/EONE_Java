package com.java.eONE.service;

import com.java.eONE.model.Classroom;
import com.java.eONE.model.BatchYear;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.BatchYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final BatchYearRepository batchYearRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository, BatchYearRepository batchYearRepository) {
        this.classroomRepository = classroomRepository;
        this.batchYearRepository = batchYearRepository;
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Classroom saveClassroom(Classroom classroom) {
        // Normalize empty strings to null for year
        String year = (classroom.getYear() != null && !classroom.getYear().trim().isEmpty()) 
            ? classroom.getYear().trim() : null;
        classroom.setYear(year);
        
        // Handle batch_year_id - if provided, fetch and set BatchYear entity
        if (classroom.getBatchYear() != null && classroom.getBatchYear().getId() != null) {
            Optional<BatchYear> batchYearOpt = batchYearRepository.findById(classroom.getBatchYear().getId());
            if (batchYearOpt.isEmpty() || !batchYearOpt.get().getIsActive()) {
                throw new IllegalArgumentException("Invalid or inactive batch year selected");
            }
            classroom.setBatchYear(batchYearOpt.get());
            // Sync batch field for backward compatibility
            classroom.setBatch(batchYearOpt.get().getName());
        } else if (classroom.getBatch() != null && !classroom.getBatch().trim().isEmpty()) {
            // Legacy support: if batch string is provided, try to find BatchYear by name
            Optional<BatchYear> batchYearOpt = batchYearRepository.findByName(classroom.getBatch().trim());
            if (batchYearOpt.isPresent() && batchYearOpt.get().getIsActive()) {
                classroom.setBatchYear(batchYearOpt.get());
            } else {
                // Keep batch as string for backward compatibility
                classroom.setBatch(classroom.getBatch().trim());
            }
        }
        
        // Check if a classroom with the same name, batch_year_id, and year already exists
        Long batchYearId = classroom.getBatchYear() != null ? classroom.getBatchYear().getId() : null;
        Optional<Classroom> existing = classroomRepository.findByNameAndBatchYearIdAndYear(
            classroom.getName(), 
            batchYearId, 
            year
        );
        
        // For new classrooms (id is null), check if any exists
        // For existing classrooms, check if another one with same combination exists
        if (existing.isPresent()) {
            if (classroom.getId() == null || !existing.get().getId().equals(classroom.getId())) {
                throw new IllegalArgumentException(
                    "A classroom with the same name, batch year, and year already exists. " +
                    "Please use a different combination."
                );
            }
        }
        
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom updateClassroom(Long id, Classroom updates) {
        return classroomRepository.findById(id).map(existing -> {
            String newName = updates.getName() != null ? updates.getName() : existing.getName();
            String newYear = updates.getYear() != null && !updates.getYear().trim().isEmpty() 
                ? updates.getYear().trim() : (existing.getYear() != null && !existing.getYear().trim().isEmpty() 
                    ? existing.getYear().trim() : null);
            
            // Handle batch_year_id update
            BatchYear newBatchYear = null;
            if (updates.getBatchYear() != null && updates.getBatchYear().getId() != null) {
                Optional<BatchYear> batchYearOpt = batchYearRepository.findById(updates.getBatchYear().getId());
                if (batchYearOpt.isEmpty() || !batchYearOpt.get().getIsActive()) {
                    throw new IllegalArgumentException("Invalid or inactive batch year selected");
                }
                newBatchYear = batchYearOpt.get();
            } else if (updates.getBatch() != null && !updates.getBatch().trim().isEmpty()) {
                // Legacy support: try to find BatchYear by name
                Optional<BatchYear> batchYearOpt = batchYearRepository.findByName(updates.getBatch().trim());
                if (batchYearOpt.isPresent() && batchYearOpt.get().getIsActive()) {
                    newBatchYear = batchYearOpt.get();
                }
            }
            
            Long newBatchYearId = newBatchYear != null ? newBatchYear.getId() : 
                (existing.getBatchYear() != null ? existing.getBatchYear().getId() : null);
            
            // Check if another classroom with the same combination exists (excluding current one)
            Optional<Classroom> duplicate = classroomRepository.findByNameAndBatchYearIdAndYear(newName, newBatchYearId, newYear);
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException(
                    "A classroom with the same name, batch year, and year already exists. " +
                    "Please use a different combination."
                );
            }
            
            // Update fields
            if (updates.getName() != null) existing.setName(updates.getName());
            if (newBatchYear != null) {
                existing.setBatchYear(newBatchYear);
                existing.setBatch(newBatchYear.getName());
            } else if (updates.getBatch() != null) {
                String normalizedBatch = updates.getBatch().trim().isEmpty() ? null : updates.getBatch().trim();
                existing.setBatch(normalizedBatch);
                existing.setBatchYear(null);
            }
            if (updates.getYear() != null) {
                String normalizedYear = updates.getYear().trim().isEmpty() ? null : updates.getYear().trim();
                existing.setYear(normalizedYear);
            }
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            return classroomRepository.save(existing);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteClassroom(Long id) {
        if (classroomRepository.existsById(id)) {
            try {
                classroomRepository.deleteById(id);
                return true;
            } catch (DataIntegrityViolationException ex) {
                return false;
            }
        }
        return false;
    }
}
