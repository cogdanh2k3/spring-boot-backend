package com.springboot.auth.controller;

import com.springboot.auth.entity.User;
import com.springboot.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Check if username already exists
            if (authService.usernameExists(request.getUsername())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Username already exists");
                errorResponse.put("user", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Check if email already exists
            if (authService.emailExists(request.getEmail())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Email already registered");
                errorResponse.put("user", null);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Register new user
            User user = authService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullName());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("user", new UserResponse(user));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            errorResponse.put("user", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Check if user exists by username or email
            Optional<User> userOpt = authService.getUserByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail());

            if (!userOpt.isPresent()) {
                // Account not found
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Account not found. Please check your username/email.");
                errorResponse.put("user", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // User exists, now verify password
            User user = userOpt.get();
            boolean passwordMatches = authService.verifyPassword(
                    request.getPassword(),
                    user.getPassword());

            if (!passwordMatches) {
                // Incorrect password
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Incorrect password. Please try again.");
                errorResponse.put("user", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Login successful - update last login time
            authService.updateLastLogin(user.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", new UserResponse(user));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Login failed: " + e.getMessage());
            errorResponse.put("user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get user profile
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        Optional<User> userOpt = authService.getUserByUsername(username);

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(new UserResponse(userOpt.get()));
        } else {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    // Update user profile
    @PutMapping("/user/{username}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String username,
            @RequestBody UpdateProfileRequest request) {
        try {
            User updatedUser = authService.updateUserProfile(
                    username,
                    request.getFullName(),
                    request.getPhoneNumber(),
                    request.getProfileImageUrl());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", new UserResponse(updatedUser));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("user", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Update user profile by ID (for mobile app)
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfileById(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request) {

        // DEBUG: Log incoming request
        System.out.println("=== CONTROLLER RECEIVED REQUEST ===");
        System.out.println("Path: PUT /api/auth/profile/" + userId);
        System.out.println("Request Full Name: " + request.getFullName());
        System.out.println("Request Email: " + request.getEmail());
        System.out.println("Request Date of Birth: " + request.getDateOfBirth());
        System.out.println("Request Hometown: " + request.getHometown());
        System.out.println("Request Phone Number: " + request.getPhoneNumber());
        System.out.println("Request CCCD: " + request.getCccd());
        System.out.println("Request CCCD Issue Date: " + request.getCccdIssueDate());
        System.out.println("Request CCCD Issue Place: " + request.getCccdIssuePlace());
        System.out.println("====================================");

        try {
            User updatedUser = authService.updateUserProfileById(
                    userId,
                    request.getFullName(),
                    request.getEmail(),
                    request.getGender(),
                    request.getDateOfBirth(),
                    request.getHometown(),
                    request.getPhoneNumber(),
                    request.getCccd(),
                    request.getCccdIssueDate(),
                    request.getCccdIssuePlace());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", new UserResponse(updatedUser));

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("user", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Change password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Check username availability
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = authService.usernameExists(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // Check email availability
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@PathVariable String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // NEW: Check if user is admin
    @GetMapping("/check-admin/{username}")
    public ResponseEntity<Map<String, Boolean>> checkAdmin(@PathVariable String username) {
        boolean isAdmin = authService.isUserAdmin(username);
        return ResponseEntity.ok(Map.of("isAdmin", isAdmin));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "auth-backend"));
    }

    // DTOs (Data Transfer Objects)
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }

    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;

        // Getters and Setters
        public String getUsernameOrEmail() {
            return usernameOrEmail;
        }

        public void setUsernameOrEmail(String usernameOrEmail) {
            this.usernameOrEmail = usernameOrEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class UpdateProfileRequest {
        private String fullName;
        private String email;
        private String phoneNumber;
        private String profileImageUrl;
        private String gender;
        private String dateOfBirth;
        private String hometown;
        private String cccd;
        private String cccdIssueDate;
        private String cccdIssuePlace;

        // Getters and Setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getHometown() {
            return hometown;
        }

        public void setHometown(String hometown) {
            this.hometown = hometown;
        }

        public String getCccd() {
            return cccd;
        }

        public void setCccd(String cccd) {
            this.cccd = cccd;
        }

        public String getCccdIssueDate() {
            return cccdIssueDate;
        }

        public void setCccdIssueDate(String cccdIssueDate) {
            this.cccdIssueDate = cccdIssueDate;
        }

        public String getCccdIssuePlace() {
            return cccdIssuePlace;
        }

        public void setCccdIssuePlace(String cccdIssuePlace) {
            this.cccdIssuePlace = cccdIssuePlace;
        }
    }

    public static class ChangePasswordRequest {
        private String username;
        private String oldPassword;
        private String newPassword;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String profileImageUrl;
        private String gender;
        private String dateOfBirth;
        private String hometown;
        private String cccd;
        private String cccdIssueDate;
        private String cccdIssuePlace;
        private String role; // NEW
        private String createdAt;
        private String lastLogin;

        public UserResponse(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.phoneNumber = user.getPhoneNumber();
            this.profileImageUrl = user.getProfileImageUrl();
            this.gender = user.getGender();
            this.dateOfBirth = user.getDateOfBirth();
            this.hometown = user.getHometown();
            this.cccd = user.getCccd();
            this.cccdIssueDate = user.getCccdIssueDate();
            this.cccdIssuePlace = user.getCccdIssuePlace();
            this.role = user.getRole(); // NEW
            this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
            this.lastLogin = user.getLastLogin() != null ? user.getLastLogin().toString() : null;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getHometown() {
            return hometown;
        }

        public void setHometown(String hometown) {
            this.hometown = hometown;
        }

        public String getCccd() {
            return cccd;
        }

        public void setCccd(String cccd) {
            this.cccd = cccd;
        }

        public String getCccdIssueDate() {
            return cccdIssueDate;
        }

        public void setCccdIssueDate(String cccdIssueDate) {
            this.cccdIssueDate = cccdIssueDate;
        }

        public String getCccdIssuePlace() {
            return cccdIssuePlace;
        }

        public void setCccdIssuePlace(String cccdIssuePlace) {
            this.cccdIssuePlace = cccdIssuePlace;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }
    }
}