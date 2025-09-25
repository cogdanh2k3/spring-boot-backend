package com.springboot.scene.repositories;
import com.springboot.scene.entity.SceneLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SceneLocationRepository extends JpaRepository<SceneLocation, Long> {
    Optional<SceneLocation> findByLocationId(String locationId);
}