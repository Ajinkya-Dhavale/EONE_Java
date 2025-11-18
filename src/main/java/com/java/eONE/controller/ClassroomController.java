package com.java.eONE.controller;

import com.java.eONE.DTO.ClassroomResponseDTO;
import com.java.eONE.enums.TeacherType;
import com.java.eONE.model.Classroom;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.repository.UserRepository;
import com.java.eONE.service.ClassroomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/v1/classrooms", produces = "application/json")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    
    @Autowired
    private ClassroomRepository classroomRepository;
    @Autowired
    private UserRepository userRepository;

//    @GetMapping
//    public ResponseEntity<List<ClassroomResponseDTO>> getAllClassrooms() {
//        List<Classroom> classrooms = classroomService.getAllClassrooms();
//        
//        List<ClassroomResponseDTO> dtos = classrooms.stream()
//                .map(c -> new ClassroomResponseDTO(c.getId(), c.getName()))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(dtos);
//        
//        
//    }
//
//    @PostMapping
//    public ResponseEntity<?> createClassroom(@RequestBody Classroom classroom) {
//        try {
//            classroom.setIsActive(true);
//            classroom.setCreatedAt(LocalDateTime.now());
//            classroom.setUpdatedAt(LocalDateTime.now());
//            
//            Classroom saved = classroomService.saveClassroom(classroom);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(new ApiResponse("Classroom created successfully",
//                        new ClassroomResponseDTO(saved.getId(), saved.getName())
//                    ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
//                    .body(new ApiError(e.getMessage()));
//        }
//    }
    @GetMapping
    public ResponseEntity<List<ClassroomResponseDTO>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();

        List<ClassroomResponseDTO> dtos = classrooms.stream()
                .map(c -> new ClassroomResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getTeacher() != null ? c.getTeacher().getName() : null, // ✅ fetch teacher name
                        c.getBatchYear() != null ? c.getBatchYear().getName() : c.getBatch(), // Use batch year name if available, else fallback to batch string
                        c.getYear()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> createClassroom(@RequestBody Classroom classroom) {
        try {
            classroom.setIsActive(true);
            classroom.setCreatedAt(LocalDateTime.now());
            classroom.setUpdatedAt(LocalDateTime.now());

            Classroom saved = classroomService.saveClassroom(classroom);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(
                            "Classroom created successfully",
                            new ClassroomResponseDTO(
                                    saved.getId(),
                                    saved.getName(),
                                    saved.getTeacher() != null ? saved.getTeacher().getName() : null,
                                    saved.getBatchYear() != null ? saved.getBatchYear().getName() : saved.getBatch(),
                                    saved.getYear()
                            )
                    ));
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage != null && errorMessage.contains("uk_classroom_name_batch_year")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiError("A classroom with the same name, batch, and year already exists. Please use a different combination."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError("Classroom with this combination already exists"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiError(e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<?> updateClassroom(@PathVariable Long id, @RequestBody Classroom classroom) {
        try {
            Classroom updated = classroomService.updateClassroom(id, classroom);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Classroom not found"));
            }
            return ResponseEntity.ok(new ApiResponse(
                    "Classroom updated successfully",
                    new ClassroomResponseDTO(
                            updated.getId(),
                            updated.getName(),
                            updated.getTeacher() != null ? updated.getTeacher().getName() : null,
                            updated.getBatchYear() != null ? updated.getBatchYear().getName() : updated.getBatch(),
                            updated.getYear()
                    )
            ));
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage != null && errorMessage.contains("uk_classroom_name_batch_year")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiError("A classroom with the same name, batch, and year already exists. Please use a different combination."));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError("Classroom with this combination already exists"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiError(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassroom(@PathVariable Long id) {
        boolean deleted = classroomService.deleteClassroom(id);
        if (deleted) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "Classroom deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Classroom not found"));
    }


    // Returns available teacher role options for registration UI based on whether class teacher is assigned
    @GetMapping("/{id}/available-teacher-roles")
    public ResponseEntity<?> availableTeacherRoles(@PathVariable Long id) {
        var opt = classroomRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Classroom not found"));
        var classroom = opt.get();
        boolean hasClassTeacher = classroom.getTeacher() != null;
        var roles = new java.util.ArrayList<String>();
        if (!hasClassTeacher) roles.add(TeacherType.CLASS_TEACHER.getCode());
        roles.add(TeacherType.SUBJECT_TEACHER.getCode());
        return ResponseEntity.ok(java.util.Map.of("classroom_id", id, "roles", roles));
    }

    // Admin: assign or change class teacher for classroom
    @PutMapping("/{id}/assign-class-teacher/{teacherId}")
    public ResponseEntity<?> assignClassTeacher(@PathVariable Long id, @PathVariable Long teacherId) {
        var classroomOpt = classroomRepository.findById(id);
        if (classroomOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Classroom not found"));
        var teacherOpt = userRepository.findById(teacherId);
        if (teacherOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Teacher not found"));

        Classroom classroom = classroomOpt.get();
        classroom.setTeacher(teacherOpt.get());
        classroom.setUpdatedAt(java.time.LocalDateTime.now());
        classroomRepository.save(classroom);

        return ResponseEntity.ok(java.util.Map.of("message", "Class teacher assigned", "classroomId", id, "teacherId", teacherId));
    }


    // Helper response classes
    static class ApiResponse {
        private String message;
        private ClassroomResponseDTO classroom;
        public ApiResponse(String message, ClassroomResponseDTO classroom) {
            this.message = message;
            this.classroom = classroom;
        }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public ClassroomResponseDTO getClassroom() { return classroom; }
        public void setClassroom(ClassroomResponseDTO classroom) { this.classroom = classroom; }
    }
    static class ApiError {
        private String error;
        public ApiError(String error) { this.error = error; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
