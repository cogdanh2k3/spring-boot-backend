package com.springboot.soundgame.repository;

import com.springboot.soundgame.entity.SoundClip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SoundClipRepository extends JpaRepository<com.springboot.soundgame.entity.SoundClip, Long> {
    Optional<SoundClip> findByClipId(String clipId);
}