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
