package com.springboot.soundgame.repository;

import com.springboot.soundgame.entity.SoundLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SoundLevelRepository extends JpaRepository<SoundLevel, Long> {
    Optional<com.springboot.soundgame.entity.SoundLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}