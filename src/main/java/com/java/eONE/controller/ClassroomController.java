package com.java.eONE.controller;

import com.java.eONE.DTO.ClassroomResponseDTO;
import com.java.eONE.model.Classroom;
import com.java.eONE.repository.ClassroomRepository;
import com.java.eONE.service.ClassroomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
    
    @Autowired
    private ClassroomRepository classroomRepository;

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
                        c.getBatch(),
                        c.getYear()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
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
                                    saved.getTeacher() != null ? saved.getTeacher().getName() : null, // ✅ teacher name
                                    saved.getBatch(),
                                    saved.getYear()
                            )
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiError(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClassroom(@PathVariable Long id, @RequestBody Classroom classroom) {
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
                        updated.getBatch(),
                        updated.getYear()
                )
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassroom(@PathVariable Long id) {
        boolean deleted = classroomService.deleteClassroom(id);
        if (deleted) {
            return ResponseEntity.ok().body(java.util.Map.of("message", "Classroom deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError("Classroom not found"));
    }


    // Helper response classes
    static class ApiResponse {
        private String message;
        private ClassroomResponseDTO classroom;
        public ApiResponse(String message, ClassroomResponseDTO classroom) {
            this.message = message;
            this.classroom = classroom;
        }
        // getters + setters
    }
    static class ApiError {
        private String error;
        public ApiError(String error) { this.error = error; }
        // getters + setters
    }
}
