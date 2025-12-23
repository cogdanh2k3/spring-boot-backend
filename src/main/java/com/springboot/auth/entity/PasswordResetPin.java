package com.springboot.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_pins")
public class PasswordResetPin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false, length = 6)
    private String pin;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    // Constructors
    public PasswordResetPin() {
    }
    
    public PasswordResetPin(String email, String pin) {
        this.email = email;
        this.pin = pin;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(15); // PIN expires after 15 minutes
        this.used = false;
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusMinutes(15);
        }
    }
    
    // Helper method
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPin() {
        return pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "PasswordResetPin{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", pin='" + pin + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                '}';
    }
}
