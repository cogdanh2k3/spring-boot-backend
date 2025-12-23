package com.springboot.auth.service;

import com.springboot.auth.entity.LoginAttempt;
import com.springboot.auth.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to track and manage login attempts
 * Implements brute-force protection by blocking users/IPs after too many failed attempts
 */
@Service
public class LoginAttemptService {
    
    @Autowired
    private LoginAttemptRepository attemptRepository;
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int COOLDOWN_MINUTES = 30;
    
    /**
     * Log a login attempt
     */
    public void logAttempt(String ipAddress, String username, boolean success) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setIpAddress(ipAddress);
        attempt.setUsername(username);
        attempt.setAttemptTime(LocalDateTime.now());
        attempt.setSuccess(success);
        attemptRepository.save(attempt);
    }
    
    /**
     * Check if user/IP is blocked due to too many failed attempts
     */
    public boolean isBlocked(String ipAddress, String username) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES);
        
        List<LoginAttempt> ipAttempts = attemptRepository
                .findByIpAddressAndSuccessFalseAndAttemptTimeAfter(ipAddress, since);
        
        List<LoginAttempt> userAttempts = attemptRepository
                .findByUsernameAndSuccessFalseAndAttemptTimeAfter(username, since);
        
        return ipAttempts.size() >= MAX_ATTEMPTS || userAttempts.size() >= MAX_ATTEMPTS;
    }
    
    /**
     * Get remaining attempts before block
     */
    public int getRemainingAttempts(String ipAddress, String username) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(COOLDOWN_MINUTES);
        
        List<LoginAttempt> attempts = attemptRepository
                .findByIpAddressAndSuccessFalseAndAttemptTimeAfter(ipAddress, since);
        
        return Math.max(0, MAX_ATTEMPTS - attempts.size());
    }
    
    /**
     * Check if captcha is required (when attempts are getting close to limit)
     */
    public boolean requiresCaptcha(String ipAddress, String username) {
        return getRemainingAttempts(ipAddress, username) <= 2;
    }
}
