package com.java.eONE.controller;

import com.java.eONE.model.Classroom;
import com.java.eONE.model.JoinRequest;
import com.java.eONE.model.User;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.JoinRequestRepository;
import com.java.eONE.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/join-requests")
public class JoinRequestController {

    @Autowired private JoinRequestRepository joinRequestRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClassroomRepository classroomRepository;

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? Long.valueOf(auth.getName()) : null;
    }

    @PostMapping("/classrooms/{classroomId}")
    public ResponseEntity<?> requestToJoin(@PathVariable Long classroomId) {
        Long userId = currentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<User> studentOpt = userRepository.findById(userId);
        Optional<Classroom> classroomOpt = classroomRepository.findById(classroomId);
        if (studentOpt.isEmpty() || classroomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid classroom or user"));
        }

        JoinRequest jr = new JoinRequest();
        jr.setStudent(studentOpt.get());
        jr.setClassroom(classroomOpt.get());
        jr.setStatus("PENDING");
        jr.setRequestedAt(LocalDateTime.now());
        joinRequestRepository.save(jr);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Join request submitted"));
    }

    @GetMapping("/classrooms/{classroomId}")
    public ResponseEntity<?> listPending(@PathVariable Long classroomId) {
        Long userId = currentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Classroom> classroomOpt = classroomRepository.findById(classroomId);
        if (classroomOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Classroom classroom = classroomOpt.get();

        if (classroom.getTeacher() == null || !classroom.getTeacher().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only class teacher can view join requests"));
        }

        List<JoinRequest> list = joinRequestRepository.findByClassroom_IdAndStatus(classroomId, "PENDING");
        return ResponseEntity.ok(list);
    }

    @PostMapping("/classrooms/{classroomId}/{reqId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long classroomId, @PathVariable Long reqId) {
        return decide(classroomId, reqId, true);
    }

    @PostMapping("/classrooms/{classroomId}/{reqId}/reject")
    public ResponseEntity<?> reject(@PathVariable Long classroomId, @PathVariable Long reqId) {
        return decide(classroomId, reqId, false);
    }

    private ResponseEntity<?> decide(Long classroomId, Long reqId, boolean approve) {
        Long userId = currentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<Classroom> classroomOpt = classroomRepository.findById(classroomId);
        if (classroomOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Classroom classroom = classroomOpt.get();

        if (classroom.getTeacher() == null || !classroom.getTeacher().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only class teacher can approve/reject"));
        }

        Optional<JoinRequest> jrOpt = joinRequestRepository.findByIdAndClassroom_Id(reqId, classroomId);
        if (jrOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Request not found"));

        JoinRequest jr = jrOpt.get();
        jr.setStatus(approve ? "APPROVED" : "REJECTED");
        jr.setApprovedByTeacher(classroom.getTeacher());
        jr.setDecidedAt(LocalDateTime.now());
        joinRequestRepository.save(jr);

        if (approve) {
            // Attach student to the classroom
            User student = jr.getStudent();
            student.setClassroom(classroom);
            userRepository.save(student);
        }

        return ResponseEntity.ok(Map.of("message", approve ? "Approved" : "Rejected"));
    }
}


