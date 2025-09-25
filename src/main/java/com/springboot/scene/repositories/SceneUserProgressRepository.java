package com.springboot.scene.repositories;
import com.springboot.scene.entity.SceneUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SceneUserProgressRepository extends JpaRepository<SceneUserProgress, Long> {
    Optional<SceneUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<SceneUserProgress> findByUsername(String username);
    List<SceneUserProgress> findByUsernameAndCompleted(String username, boolean completed);
}