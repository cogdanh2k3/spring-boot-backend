package com.springboot.auth.repository;

import com.springboot.auth.entity.PasswordResetPin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetPinRepository extends JpaRepository<PasswordResetPin, Long> {
    
    Optional<PasswordResetPin> findByEmailAndPinAndUsedFalse(String email, String pin);
    
    void deleteByEmail(String email);
}
