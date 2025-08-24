package com.java.eONE.controller;

import com.java.eONE.model.Role;
import com.java.eONE.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/roles")
public class RolesController {

    private static final String ADMIN = "ADMIN";  // Same constant name as in Ruby

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<Role>> getRoles() {
        List<Role> roles = roleRepository.findByNameNot(ADMIN);
        return ResponseEntity.ok(roles);
    }
}
