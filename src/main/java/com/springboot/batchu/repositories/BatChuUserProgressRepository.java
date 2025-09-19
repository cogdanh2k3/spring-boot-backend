package com.springboot.batchu.repositories;

import com.springboot.batchu.entity.batChuUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatChuUserProgressRepository extends JpaRepository<batChuUserProgress, Long> {
    Optional<batChuUserProgress> findByUsernameAndLevelId(String username, String levelId);
    List<batChuUserProgress> findByUsername(String username);
    List<batChuUserProgress> findByUsernameAndIsCompleted(String username, Boolean isComplete);
}