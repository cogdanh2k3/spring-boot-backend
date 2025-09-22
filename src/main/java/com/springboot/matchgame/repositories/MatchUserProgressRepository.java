package com.springboot.matchgame.repositories;

import com.springboot.matchgame.entity.MatchUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchUserProgressRepository extends JpaRepository<MatchUserProgress, Long> {
    Optional<MatchUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<MatchUserProgress> findByUsername(String username);
    List<MatchUserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}