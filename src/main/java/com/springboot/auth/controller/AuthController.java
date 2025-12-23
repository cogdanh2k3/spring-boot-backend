package com.springboot.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.auth.controller.AuthController.ChangePasswordRequest;
import com.springboot.auth.controller.AuthController.UpdateProfileRequest;
import com.springboot.auth.controller.AuthController.UserResponse;
import com.springboot.auth.entity.User;
import com.springboot.auth.security.JwtTokenProvider;
import com.springboot.auth.service.AuthService;
import com.springboot.auth.service.LoginAttemptService;
import com.springboot.auth.service.PasswordResetService;
import com.springboot.auth.service.RecaptchaService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Autowired
    private RecaptchaService recaptchaService;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = getClientIP(httpRequest);

        try {
            // Check if blocked
            if (loginAttemptService.isBlocked(ipAddress, request.getUsernameOrEmail())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Too many failed attempts. Please try again in 30 minutes.");
                errorResponse.put("user", null);
                errorResponse.put("requiresCaptcha", true);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
            }

            // NEW: Check remaining attempts to decide if captcha is required
            int remaining = loginAttemptService.getRemainingAttempts(ipAddress, request.getUsernameOrEmail());
            boolean requiresCaptcha = remaining <= 2;

            // NEW: Verify captcha if required and provided
            if (requiresCaptcha && request.getCaptchaToken() != null && !request.getCaptchaToken().isEmpty()) {
                boolean captchaValid = recaptchaService.verifyToken(request.getCaptchaToken());
                if (!captchaValid) {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Invalid reCAPTCHA. Please try again.");
                    errorResponse.put("user", null);
                    errorResponse.put("remainingAttempts", remaining);
                    errorResponse.put("requiresCaptcha", true);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
                System.out.println("✅ reCAPTCHA verified successfully for user: " + request.getUsernameOrEmail());
            }
            // Check if user exists by username or email
            Optional<User> userOpt = authService.getUserByUsernameOrEmail(
                    request.getUsernameOrEmail(),
                    request.getUsernameOrEmail());

            if (!userOpt.isPresent()) {
                // Account not found - log failed attempt
                loginAttemptService.logAttempt(ipAddress, request.getUsernameOrEmail(), false);
                remaining = loginAttemptService.getRemainingAttempts(ipAddress, request.getUsernameOrEmail());

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Account not found. Please check your username/email.");
                errorResponse.put("user", null);
                errorResponse.put("remainingAttempts", remaining);
                errorResponse.put("requiresCaptcha", remaining <= 2);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // User exists, now verify password
            User user = userOpt.get();
            boolean passwordMatches = authService.verifyPassword(
                    request.getPassword(),
                    user.getPassword());

            if (!passwordMatches) {
                // Incorrect password - log failed attempt
                loginAttemptService.logAttempt(ipAddress, request.getUsernameOrEmail(), false);
                remaining = loginAttemptService.getRemainingAttempts(ipAddress, request.getUsernameOrEmail());

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Incorrect password. Please try again.");
                errorResponse.put("user", null);
                errorResponse.put("remainingAttempts", remaining);
                errorResponse.put("requiresCaptcha", remaining <= 2);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Login successful - log successful attempt and update last login time
            loginAttemptService.logAttempt(ipAddress, request.getUsernameOrEmail(), true);
            authService.updateLastLogin(user.getId());

            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", new UserResponse(user));
            // JWT tokens
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("accessTokenExpiresIn", 900); // 15 minutes in seconds
            response.put("refreshTokenExpiresIn", 604800); // 7 days in seconds

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Login failed: " + e.getMessage());
            errorResponse.put("user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Helper method to get client IP
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
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

    // NEW: Forgot password - send PIN
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String message = passwordResetService.generateResetPin(request.get("email"));
            return ResponseEntity.ok(Map.of("success", true, "message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // NEW: Verify PIN and reset password
    @PostMapping("/verify-pin")
    public ResponseEntity<?> verifyPinAndResetPassword(@RequestBody Map<String, String> request) {
        try {
            String message = passwordResetService.verifyPinAndResetPassword(
                    request.get("email"),
                    request.get("pin"),
                    request.get("newPassword"));
            return ResponseEntity.ok(Map.of("success", true, "message", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // JWT Token Refresh endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Refresh token is required"));
            }

            // Validate refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Invalid or expired refresh token"));
            }

            // Check if it's a refresh token (not access token)
            String tokenType = jwtTokenProvider.getTokenType(refreshToken);
            if (!"refresh".equals(tokenType)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Invalid token type"));
            }

            // Extract user info and generate new access token
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

            // Get user to check role
            Optional<User> userOpt = authService.getUserByUsername(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "User not found"));
            }

            User user = userOpt.get();
            String newAccessToken = jwtTokenProvider.generateAccessToken(userId, username, user.getRole());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "accessToken", newAccessToken,
                    "accessTokenExpiresIn", 900 // 15 minutes
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Token refresh failed: " + e.getMessage()));
        }
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
        private String captchaToken;

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

        public String getCaptchaToken() {
            return captchaToken;
        }

        public void setCaptchaToken(String captchaToken) {
            this.captchaToken = captchaToken;
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