package com.springboot.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts", indexes = {
    @Index(name = "idx_ip_time", columnList = "ipAddress,attemptTime"),
    @Index(name = "idx_user_time", columnList = "username,attemptTime")
})
public class LoginAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String ipAddress;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private LocalDateTime attemptTime;
    
    @Column(nullable = false)
    private boolean success;
    
    // Constructors
    public LoginAttempt() {
    }
    
    public LoginAttempt(String ipAddress, String username, boolean success) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.attemptTime = LocalDateTime.now();
        this.success = success;
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.attemptTime == null) {
            this.attemptTime = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }
    
    public void setAttemptTime(LocalDateTime attemptTime) {
        this.attemptTime = attemptTime;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "LoginAttempt{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", username='" + username + '\'' +
                ", attemptTime=" + attemptTime +
                ", success=" + success +
                '}';
    }
}
