package com.springboot.matchgame.repositories;

import com.springboot.matchgame.entity.MatchLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchLevelRepository extends JpaRepository<MatchLevel, Long> {
    Optional<MatchLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}