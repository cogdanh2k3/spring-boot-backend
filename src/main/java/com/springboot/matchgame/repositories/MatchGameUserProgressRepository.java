package com.springboot.matchgame.repositories;

import com.springboot.matchgame.entity.MatchGameUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchGameUserProgressRepository extends JpaRepository<MatchGameUserProgress, Long> {
    Optional<MatchGameUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<MatchGameUserProgress> findByUsername(String username);
    List<MatchGameUserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}