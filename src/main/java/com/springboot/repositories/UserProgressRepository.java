package com.springboot.repositories;

import com.springboot.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUsernameAndTopicId(String username, String topicId);
    List<UserProgress> findByUsername(String username);
    List<UserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}
