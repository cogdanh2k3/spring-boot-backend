package com.springboot.scene.repositories;
import com.springboot.scene.entity.SceneLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SceneLevelRepository extends JpaRepository<SceneLevel, Long> {
    Optional<SceneLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}