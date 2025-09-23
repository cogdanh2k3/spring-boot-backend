package com.springboot.quizgame.repositories;

import com.springboot.quizgame.entity.QuizUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizUserProgressRepository extends JpaRepository<QuizUserProgress, Long> {
    Optional<QuizUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<QuizUserProgress> findByUsername(String username);
    List<QuizUserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}