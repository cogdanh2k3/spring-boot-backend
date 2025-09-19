package com.springboot.quizgame.repositories;

import com.springboot.quizgame.entity.QuizGameUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizGameUserProgressRepository extends JpaRepository<QuizGameUserProgress, Long> {
    Optional<QuizGameUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<QuizGameUserProgress> findByUsername(String username);
    List<QuizGameUserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}