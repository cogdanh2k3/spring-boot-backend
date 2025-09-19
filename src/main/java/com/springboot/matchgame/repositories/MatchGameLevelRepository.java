package com.springboot.matchgame.repositories;

import com.springboot.matchgame.entity.MatchGameLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchGameLevelRepository extends JpaRepository<MatchGameLevel, Long> {
    Optional<MatchGameLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}