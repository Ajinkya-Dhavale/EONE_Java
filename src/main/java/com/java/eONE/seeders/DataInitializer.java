package com.java.eONE.seeders;

import com.java.eONE.model.Role;
import com.java.eONE.model.User;
import com.java.eONE.repository.RoleRepository;
import com.java.eONE.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();

        // Create roles if not exist
        Role student = roleRepository.findByName("Student");
        if (student == null) {
            student = new Role();
            student.setName("Student");
            student.setCreatedAt(now);
            student.setUpdatedAt(now);
            student = roleRepository.save(student);
        }

        Role admin = roleRepository.findByName("ADMIN");
        if (admin == null) {
            admin = new Role();
            admin.setName("ADMIN");
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            admin = roleRepository.save(admin);
        }

        Role teacher = roleRepository.findByName("Teacher");
        if (teacher == null) {
            teacher = new Role();
            teacher.setName("Teacher");
            teacher.setCreatedAt(now);
            teacher.setUpdatedAt(now);
            teacher = roleRepository.save(teacher);
        }

        // Create admin user if not exist
        Optional<User> adminUserOpt = userRepository.findAll()
            .stream()
            .filter(u -> u.getEmail().equalsIgnoreCase("admin@gmail.com"))
            .findFirst();

        if (adminUserOpt.isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setName("Admin");
            adminUser.setPasswordDigest("admin@123"); // Hash in real app!
            adminUser.setMobileNumber("960492407");
            adminUser.setStatus(1); // e.g., 1 for approved
            adminUser.setRole(admin);
            userRepository.save(adminUser);
        }
    }
}
