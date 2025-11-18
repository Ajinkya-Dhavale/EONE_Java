package com.java.eONE.controller;

import com.java.eONE.model.Classroom;
import com.java.eONE.model.Subject;
import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.SubjectRepository;
import com.java.eONE.repository.UserRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/schedule")
public class ScheduleController {

    @Autowired private UserRepository userRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ClassroomRepository classroomRepository;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? Long.valueOf(auth.getName()) : null;
    }

    private String todayKey() {
        DayOfWeek dow = LocalDate.now().getDayOfWeek();
        // subjects.daysList appears to store names like Monday, Tuesday, ...
        return switch (dow) {
            case MONDAY -> "Monday";
            case TUESDAY -> "Tuesday";
            case WEDNESDAY -> "Wednesday";
            case THURSDAY -> "Thursday";
            case FRIDAY -> "Friday";
            case SATURDAY -> "Saturday";
            case SUNDAY -> "Sunday";
        };
    }

    @GetMapping("/teachers/me/today")
    public ResponseEntity<?> teacherToday() {
        Long userId = currentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        Optional<User> teacherOpt = userRepository.findById(userId);
        if (teacherOpt.isEmpty()) return ResponseEntity.status(404).build();

        User teacher = teacherOpt.get();
        Classroom classroom = teacher.getClassroom();
        List<Subject> subjects;
        if (classroom != null) {
            subjects = classroom.getSubjects();
        } else {
            // Fallback: gather subjects from teacher's associated classrooms via subjects they teach
            // This lets subject teachers without a direct classroom assignment still see today's schedule
            subjects = subjectRepository.findByTeacherId(teacher.getId());
            // If teacher has no subjects, return empty list
            if (subjects == null || subjects.isEmpty()) {
                return ResponseEntity.ok(List.of());
            }
            // Expand to include all subjects in those classrooms (not only owned by teacher)
            var classroomIds = subjects.stream()
                .map(s -> s.getClassroom() != null ? s.getClassroom().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
            List<Subject> all = new java.util.ArrayList<>();
            for (Long cid : classroomIds) {
                all.addAll(subjectRepository.findByClassroomId(cid));
            }
            subjects = all;
        }
        String today = todayKey();
        List<Map<String, Object>> periods = subjects.stream()
            .filter(s -> s.getDaysList() != null && Arrays.asList(s.getDaysList()).contains(today))
            .map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("subject_id", s.getId());
                map.put("subject_name", s.getName());
                map.put("start_time", s.getStartTime());
                map.put("end_time", s.getEndTime());
                map.put("teacher_id", s.getTeacher() != null ? s.getTeacher().getId() : null);
                return map;
            })
            .sorted(Comparator.comparing(m -> (String)m.get("start_time")))
            .collect(Collectors.toList());
        return ResponseEntity.ok(periods);
    }

    @GetMapping("/students/me/today")
    public ResponseEntity<?> studentToday() {
        Long userId = currentUserId();
        if (userId == null) return ResponseEntity.status(401).build();
        Optional<User> studentOpt = userRepository.findById(userId);
        if (studentOpt.isEmpty()) return ResponseEntity.status(404).build();
        User student = studentOpt.get();
        Classroom classroom = student.getClassroom();
        if (classroom == null) return ResponseEntity.ok(List.of());

        List<Subject> subjects = classroom.getSubjects();
        String today = todayKey();
        List<Map<String, Object>> periods = subjects.stream()
            .filter(s -> s.getDaysList() != null && Arrays.asList(s.getDaysList()).contains(today))
            .map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("subject_id", s.getId());
                map.put("subject_name", s.getName());
                map.put("start_time", s.getStartTime());
                map.put("end_time", s.getEndTime());
                map.put("teacher_id", s.getTeacher() != null ? s.getTeacher().getId() : null);
                map.put("teacher_name", s.getTeacher() != null ? s.getTeacher().getName() : null);
                return map;
            })
            .sorted(Comparator.comparing(m -> (String)m.get("start_time")))
            .collect(Collectors.toList());
        return ResponseEntity.ok(periods);
    }
}