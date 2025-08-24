package com.java.eONE.service;

import com.java.eONE.model.Classroom;
import com.java.eONE.repository.ClassroomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
