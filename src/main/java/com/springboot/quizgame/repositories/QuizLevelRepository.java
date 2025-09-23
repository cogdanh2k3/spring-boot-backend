package com.springboot.quizgame.repositories;

import com.springboot.quizgame.entity.QuizLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizLevelRepository extends JpaRepository<QuizLevel, Long> {
    Optional<QuizLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}