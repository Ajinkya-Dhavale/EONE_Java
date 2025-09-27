package com.java.eONE.controller;

import com.java.eONE.DTO.UserResponseDTO;
import com.java.eONE.model.User;
import com.java.eONE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userService.authenticate(email, password);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        User user = userOpt.get();

        if (!userService.isApproved(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User approval is still pending"));
        }

        // Construct response DTO without token
        String avatarUrl = (user.getAvatar() != null && !user.getAvatar().isEmpty())
                ? "/uploads/" + user.getAvatar()
                : null;
        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getMobileNumber(),
                user.getStatus(),
                user.getDateOfBirth(),
                user.getRole().getName(),
                user.getClassroom() != null ? user.getClassroom().getName() : null,
                user.getClassroom() != null ? user.getClassroom().getId() : null,
                null,
                avatarUrl
        );

        return ResponseEntity.ok(Map.of("user", responseDTO));
    }
}
