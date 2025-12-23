package com.springboot.auth.repository;

import com.springboot.auth.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    
    List<LoginAttempt> findByIpAddressAndSuccessFalseAndAttemptTimeAfter(
            String ipAddress, 
            LocalDateTime since
    );
    
    List<LoginAttempt> findByUsernameAndSuccessFalseAndAttemptTimeAfter(
            String username, 
            LocalDateTime since
    );
}
