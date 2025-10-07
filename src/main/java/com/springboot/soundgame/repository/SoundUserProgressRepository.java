package com.springboot.soundgame.repository;

import com.springboot.soundgame.entity.SoundUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SoundUserProgressRepository extends JpaRepository<SoundUserProgress, Long> {
    Optional<SoundUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<SoundUserProgress> findByUsername(String username);
    List<SoundUserProgress> findByUsernameAndCompleted(String username, boolean completed);
}

