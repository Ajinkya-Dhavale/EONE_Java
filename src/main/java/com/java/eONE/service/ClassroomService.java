package com.java.eONE.service;

import com.java.eONE.model.Classroom;
import com.java.eONE.repository.ClassroomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Classroom saveClassroom(Classroom classroom) {
        // You can add validations here if needed
        return classroomRepository.save(classroom);
    }

    @Transactional
    public Classroom updateClassroom(Long id, Classroom updates) {
        return classroomRepository.findById(id).map(existing -> {
            if (updates.getName() != null) existing.setName(updates.getName());
            if (updates.getBatch() != null) existing.setBatch(updates.getBatch());
            if (updates.getYear() != null) existing.setYear(updates.getYear());
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
