package com.java.eONE.seeders;

import com.java.eONE.enums.RoleType;
import com.java.eONE.enums.UserStatus;
import com.java.eONE.model.Role;
import com.java.eONE.model.User;
import com.java.eONE.repository.RoleRepository;
import com.java.eONE.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

     @Autowired
    private PasswordEncoder passwordEncoder;

    // Admin credentials from environment variables (with defaults for development)
    @Value("${admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.name:Admin}")
    private String adminName;

    @Value("${admin.mobile:960492407}")
    private String adminMobile;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();

        // Create roles if not exist using RoleType enum
        Role student = roleRepository.findByName(RoleType.STUDENT.getCode());
        if (student == null) {
            student = new Role();
            student.setName(RoleType.STUDENT.getCode());
            student.setCreatedAt(now);
            student.setUpdatedAt(now);
            student = roleRepository.save(student);
        }

        Role admin = roleRepository.findByName(RoleType.ADMIN.getCode());
        if (admin == null) {
            admin = new Role();
            admin.setName(RoleType.ADMIN.getCode());
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            admin = roleRepository.save(admin);
        }

        Role teacher = roleRepository.findByName(RoleType.TEACHER.getCode());
        if (teacher == null) {
            teacher = new Role();
            teacher.setName(RoleType.TEACHER.getCode());
            teacher.setCreatedAt(now);
            teacher.setUpdatedAt(now);
            teacher = roleRepository.save(teacher);
        }

        // Create admin user if not exist (using environment variables)
        Optional<User> adminUserOpt = userRepository.findAll()
            .stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(adminEmail))
            .findFirst();

        if (adminUserOpt.isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setName(adminName);
            adminUser.setPasswordDigest(passwordEncoder.encode(adminPassword));
            adminUser.setMobileNumber(adminMobile);
            adminUser.setStatus(UserStatus.APPROVED.getValue()); // Approved status
            adminUser.setRole(admin);
            userRepository.save(adminUser);
        }
    }
}
