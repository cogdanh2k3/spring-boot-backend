package com.springboot.quizgame.repositories;

import com.springboot.quizgame.entity.QuizGameLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizGameLevelRepository extends JpaRepository<QuizGameLevel, Long> {
    Optional<QuizGameLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}