package com.springboot.auth.service;

import com.springboot.auth.entity.PasswordResetPin;
import com.springboot.auth.entity.User;
import com.springboot.auth.repository.PasswordResetPinRepository;
import com.springboot.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * Service for handling password reset via PIN
 */
@Service
public class PasswordResetService {
    
    @Autowired
    private PasswordResetPinRepository pinRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Generate and send reset PIN to user's email
     */
    @Transactional
    public String generateResetPin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        // Delete old PINs for this email
        pinRepository.deleteByEmail(email);
        
        // Generate random 6-digit PIN
        String pin = String.format("%06d", new Random().nextInt(999999));
        
        PasswordResetPin resetPin = new PasswordResetPin(email, pin);
        pinRepository.save(resetPin);
        
        // Send email
        sendPinEmail(email, pin);
        
        return "PIN sent to your email";
    }
    
    /**
     * Send PIN via email
     */
    private void sendPinEmail(String email, String pin) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset PIN - EduQuizz");
            message.setText("Your password reset PIN is: " + pin + 
                    "\n\nThis PIN will expire in 15 minutes." +
                    "\n\nIf you did not request this, please ignore this email.");
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    /**
     * Verify PIN and reset password
     */
    @Transactional
    public String verifyPinAndResetPassword(String email, String pin, String newPassword) {
        PasswordResetPin resetPin = pinRepository.findByEmailAndPinAndUsedFalse(email, pin)
                .orElseThrow(() -> new RuntimeException("Invalid or expired PIN"));
        
        if (resetPin.isExpired()) {
            throw new RuntimeException("PIN has expired");
        }
        
        // Change password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark PIN as used
        resetPin.setUsed(true);
        pinRepository.save(resetPin);
        
        return "Password reset successfully";
    }
}
