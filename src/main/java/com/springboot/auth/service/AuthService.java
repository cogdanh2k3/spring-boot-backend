package com.springboot.auth.service;

import com.springboot.auth.entity.User;
import com.springboot.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Register new user
    public User registerUser(String username, String email, String password, String fullName) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(fullName != null ? fullName.trim() : null);
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);
        user.setRole("USER"); // Default role

        return userRepository.save(user);
    }

    // Get user by username or email (dùng cho login check)
    public Optional<User> getUserByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }

    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    // Update user profile
    public User updateUserProfile(String username, String fullName, String phoneNumber, String profileImageUrl) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        if (fullName != null) {
            user.setFullName(fullName.trim());
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.trim());
        }
        if (profileImageUrl != null) {
            user.setProfileImageUrl(profileImageUrl.trim());
        }

        return userRepository.save(user);
    }

    // Change password
    public void changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Update last login time
    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    // Verify password (public method để Controller có thể dùng)
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    // NEW: Check if user is admin
    public boolean isUserAdmin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(User::isAdmin).orElse(false);
    }

    // NEW: Set user role (only for admin operations)
    public void setUserRole(String username, String role) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setRole(role);
        userRepository.save(user);
    }
}