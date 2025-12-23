package com.springboot.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.auth.entity.User;
import com.springboot.auth.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    @Autowired
    public AuthService(UserRepository userRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.encryptionService = encryptionService;
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

    // Update user profile by ID (for mobile app)
    public User updateUserProfileById(Long userId, String fullName, String email, String gender, String dateOfBirth,
            String hometown, String phoneNumber, String cccd, String cccdIssueDate, String cccdIssuePlace) {

        // DEBUG: Log incoming parameters
        System.out.println("=== UPDATE PROFILE DEBUG ===");
        System.out.println("User ID: " + userId);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Gender: " + gender);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Hometown: " + hometown);
        System.out.println("Phone Number (raw): " + phoneNumber);
        System.out.println("CCCD (raw): " + cccd);
        System.out.println("CCCD Issue Date (raw): " + cccdIssueDate);
        System.out.println("CCCD Issue Place (raw): " + cccdIssuePlace);

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Update EDITABLE non-sensitive fields
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
            System.out.println("✓ Updated fullName");
        }

        if (email != null && !email.trim().isEmpty()) {
            // Check if email already exists for another user
            Optional<User> existingUser = userRepository.findByEmail(email.trim());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email.trim());
            System.out.println("✓ Updated email");
        }

        if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
            user.setDateOfBirth(dateOfBirth.trim());
            System.out.println("✓ Updated dateOfBirth");
        }

        if (hometown != null && !hometown.trim().isEmpty()) {
            user.setHometown(hometown.trim());
            System.out.println("✓ Updated hometown");
        }

        // Gender - keep existing value (not editable from EditProfileScreen)
        if (gender != null && !gender.trim().isEmpty()) {
            user.setGender(gender.trim());
            System.out.println("✓ Updated gender");
        }

        // Update EDITABLE sensitive field (encrypted) - phoneNumber
        System.out.println("=== ENCRYPTING EDITABLE SENSITIVE FIELDS ===");
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            System.out.println("Encrypting phone number: " + phoneNumber.trim());
            String encryptedPhone = encryptionService.encryptData(phoneNumber.trim());
            System.out.println("Encrypted phone (first 50 chars): "
                    + encryptedPhone.substring(0, Math.min(50, encryptedPhone.length())));
            user.setPhoneNumber(encryptedPhone);
            System.out.println("✓ Updated phoneNumber (encrypted)");
        } else {
            System.out.println("Phone number is NULL or empty - skipping");
        }

        // READ-ONLY sensitive fields (encrypted) - CCCD data
        // These fields are accepted in the request but NOT updated if already set
        System.out.println("=== CHECKING READ-ONLY FIELDS (CCCD) ===");

        if (cccd != null && !cccd.trim().isEmpty()) {
            // Only update if CCCD is not already set (initial setup)
            if (user.getCccd() == null || user.getCccd().trim().isEmpty()) {
                System.out.println("CCCD not set yet - encrypting and saving: " + cccd.trim());
                String encryptedCccd = encryptionService.encryptData(cccd.trim());
                System.out.println("Encrypted CCCD (first 50 chars): "
                        + encryptedCccd.substring(0, Math.min(50, encryptedCccd.length())));
                user.setCccd(encryptedCccd);
                System.out.println("✓ Initial CCCD setup completed");
            } else {
                System.out.println("⚠ CCCD already set - IGNORING update (read-only protection)");
            }
        } else {
            System.out.println("CCCD is NULL or empty - skipping");
        }

        if (cccdIssueDate != null && !cccdIssueDate.trim().isEmpty()) {
            // Only update if CCCD Issue Date is not already set
            if (user.getCccdIssueDate() == null || user.getCccdIssueDate().trim().isEmpty()) {
                System.out.println("CCCD Issue Date not set yet - encrypting: " + cccdIssueDate.trim());
                String encryptedIssueDate = encryptionService.encryptData(cccdIssueDate.trim());
                user.setCccdIssueDate(encryptedIssueDate);
                System.out.println("✓ Initial CCCD Issue Date setup completed");
            } else {
                System.out.println("⚠ CCCD Issue Date already set - IGNORING update (read-only protection)");
            }
        } else {
            System.out.println("CCCD Issue Date is NULL or empty - skipping");
        }

        if (cccdIssuePlace != null && !cccdIssuePlace.trim().isEmpty()) {
            // Only update if CCCD Issue Place is not already set
            if (user.getCccdIssuePlace() == null || user.getCccdIssuePlace().trim().isEmpty()) {
                System.out.println("CCCD Issue Place not set yet - encrypting: " + cccdIssuePlace.trim());
                String encryptedIssuePlace = encryptionService.encryptData(cccdIssuePlace.trim());
                user.setCccdIssuePlace(encryptedIssuePlace);
                System.out.println("✓ Initial CCCD Issue Place setup completed");
            } else {
                System.out.println("⚠ CCCD Issue Place already set - IGNORING update (read-only protection)");
            }
        } else {
            System.out.println("CCCD Issue Place is NULL or empty - skipping");
        }

        System.out.println("=== SAVING USER TO DATABASE ===");
        User savedUser = userRepository.save(user);
        System.out.println(
                "User saved successfully. Phone in DB: " + (savedUser.getPhoneNumber() != null ? "ENCRYPTED" : "NULL"));
        System.out.println(
                "User saved successfully. CCCD in DB: " + (savedUser.getCccd() != null ? "ENCRYPTED" : "NULL"));
        System.out.println("=== END UPDATE PROFILE DEBUG ===");

        return savedUser;
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

    // Alias for isUserAdmin
    public boolean checkAdminRole(String username) {
        return isUserAdmin(username);
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