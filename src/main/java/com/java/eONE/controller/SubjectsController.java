package com.java.eONE.controller;

import com.java.eONE.DTO.SubjectDTO;
import com.java.eONE.model.Classroom;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/subjects")
public class SubjectsController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired private ClassroomRepository classroomRepository;

    @PostMapping
    public ResponseEntity<?> createSubject(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        List<String> daysList = (List<String>) body.get("days_list");
        String startTime = (String) body.get("start_time");
        String endTime = (String) body.get("end_time");
        Long teacherId = Long.valueOf(body.get("teacher_id").toString());
        Long classroomId = Long.valueOf(body.get("classroom_id").toString());

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new RuntimeException("Classroom not found"));

        Subject subject = new Subject();
        subject.setName(name);
        subject.setDaysList(daysList != null ? daysList.toArray(new String[0]) : new String[0]);
        subject.setStartTime(startTime);
        subject.setEndTime(endTime);
        subject.setTeacher(teacher);
        subject.setClassroom(classroom);
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());

        SubjectDTO savedDto = subjectService.createSubject(subject);

        return ResponseEntity.status(201).body(
                Map.of("message", "Subject created successfully", "subject", savedDto)
        );
    }



    @GetMapping("/{id}/subjects")
    public ResponseEntity<?> getSubjectsByTeacher(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        List<SubjectDTO> subjectsDto = subjectService.getSubjectsByTeacherId(user.getId());
        return ResponseEntity.ok(subjectsDto);
    }

}
