package com.springboot.auth.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validator for password strength checking
 * Requirements:
 * - Minimum 8 characters
 * - At least 1 uppercase letter
 * - At least 1 number
 * - At least 1 special character
 */
@Component
public class PasswordValidator {
    
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        
        public ValidationResult() {
            this.errors = new ArrayList<>();
        }
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        // Getters and Setters
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }
    
    public ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.length() < 8) {
            errors.add("Password must be at least 8 characters");
        }
        
        if (password != null && !password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least 1 uppercase letter");
        }
        
        if (password != null && !password.matches(".*[0-9].*")) {
            errors.add("Password must contain at least 1 number");
        }
        
        if (password != null && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            errors.add("Password must contain at least 1 special character");
        }
        
        ValidationResult result = new ValidationResult();
        result.setValid(errors.isEmpty());
        result.setErrors(errors);
        return result;
    }
}
