package com.java.eONE.service;

import com.java.eONE.DTO.SubjectDTO;
import com.java.eONE.model.Subject;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Transactional
    public SubjectDTO createSubject(Subject subject) {
        // Check for duplicate subject name for the same teacher and classroom
        if (subject.getTeacher() != null && subject.getClassroom() != null) {
            java.util.Optional<Subject> existing = subjectRepository.findByTeacherIdAndClassroomIdAndNameIgnoreCase(
                    subject.getTeacher().getId(),
                    subject.getClassroom().getId(),
                    subject.getName().trim()
            );
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Subject with name '" + subject.getName() + "' already exists for this teacher in this classroom.");
            }
        }

        Subject savedSubject = subjectRepository.save(subject);
        return toDTO(savedSubject);
    }

    public List<SubjectDTO> getSubjectsByTeacherId(Long teacherId) {
        List<Subject> subjects = subjectRepository.findByTeacherId(teacherId);
        return subjects.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SubjectDTO> getSubjectsByClassroomId(Long classroomId) {
        List<Subject> subjects = subjectRepository.findByClassroomId(classroomId);
        return subjects.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteSubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            return false;
        }
        subjectRepository.deleteById(subjectId);
        return true;
    }

    // Convert Subject entity to SubjectDTO
    public SubjectDTO toDTO(Subject subject) {
        return new SubjectDTO(
                subject.getId(),
                subject.getName(),
                subject.getStartTime(),
                subject.getEndTime(),
                subject.getTeacher() != null ? subject.getTeacher().getId() : null,
                subject.getClassroom() != null ? subject.getClassroom().getId() : null,
                subject.getDaysList() != null ? java.util.Arrays.asList(subject.getDaysList()) : java.util.List.of()
        );
    }
}
