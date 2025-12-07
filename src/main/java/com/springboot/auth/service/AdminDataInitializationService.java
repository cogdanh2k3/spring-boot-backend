package com.springboot.auth.service;

import com.springboot.auth.entity.User;
import com.springboot.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AdminDataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        initializeAdminAccount();
    }

    private void initializeAdminAccount() {
        // Check if admin account already exists
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isPresent()) {
            System.out.println("Admin account already exists");
            return;
        }

        System.out.println("Creating default admin account...");

        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@eduquiz.com");
        admin.setPassword(passwordEncoder.encode("admin123")); // Default password
        admin.setFullName("System Administrator");
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setLastLogin(LocalDateTime.now());

        userRepository.save(admin);

        System.out.println("================================");
        System.out.println("Admin account created successfully!");
        System.out.println("Username: admin");
        System.out.println("Password: admin123");
        System.out.println("PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN!");
        System.out.println("================================");
    }
}