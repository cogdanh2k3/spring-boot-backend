package com.springboot.batchu.repositories;

import com.springboot.batchu.entity.batChuLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatChuLevelRepository extends JpaRepository<batChuLevel, Long> {
    Optional<batChuLevel> findByLevelId(String levelId);
    Boolean existsByLevelId(String levelId);
}