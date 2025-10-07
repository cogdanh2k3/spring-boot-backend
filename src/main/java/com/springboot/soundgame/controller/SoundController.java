package com.springboot.soundgame.controller;

import com.springboot.soundgame.entity.SoundClip;
import com.springboot.soundgame.entity.SoundLevel;
import com.springboot.soundgame.entity.SoundUserProgress;
import com.springboot.soundgame.service.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sound")
@CrossOrigin(origins = "*")
public class SoundController {

    @Autowired
    private SoundService soundService;

    // Get all levels
    @GetMapping("/levels")
    public List<SoundLevel> getAllLevels() {
        return soundService.getAllLevels();
    }

    // Get level by levelId (LevelEasy, LevelNormal...)
    @GetMapping("/levels/{levelId}")
    public ResponseEntity<SoundLevel> getLevel(@PathVariable String levelId) {
        Optional<SoundLevel> lvl = soundService.getLevelById(levelId);
        return lvl.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get random clip from level
    @GetMapping("/levels/{levelId}/random")
    public ResponseEntity<SoundClip> getRandomClip(@PathVariable String levelId) {
        Optional<SoundClip> clip = soundService.getRandomClipFromLevel(levelId);
        return clip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Check answer
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkAnswer(@RequestParam String clipId, @RequestParam String answer) {
        boolean ok = soundService.checkAnswer(clipId, answer);
        return ResponseEntity.ok(ok);
    }

    // ===== USER PROGRESS ENDPOINTS (NEW) =====

    // Save level completion
    @PostMapping("/progress")
    public ResponseEntity<SoundUserProgress> saveLevelCompletion(@RequestBody SoundUserProgress progress) {
        SoundUserProgress saved = soundService.saveUserProgress(progress);
        return ResponseEntity.ok(saved);
    }

    // Get level completion status
    @GetMapping("/progress/{userName}/{levelId}")
    public ResponseEntity<SoundUserProgress> getLevelCompletion(
            @PathVariable String userName,
            @PathVariable String levelId) {
        Optional<SoundUserProgress> progress = soundService.getUserProgress(userName, levelId);
        return progress.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(new SoundUserProgress(userName, levelId, false, null)));
    }

    // Get all progress for a user
    @GetMapping("/progress/{userName}")
    public ResponseEntity<List<SoundUserProgress>> getUserProgress(@PathVariable String userName) {
        List<SoundUserProgress> progressList = soundService.getAllUserProgress(userName);
        return ResponseEntity.ok(progressList);
    }

    // ===== ADMIN ENDPOINTS =====

    @PostMapping("/admin/levels")
    public ResponseEntity<SoundLevel> createLevel(@RequestBody SoundLevel level) {
        SoundLevel created = soundService.createLevel(level);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/admin/levels/{levelId}")
    public ResponseEntity<SoundLevel> updateLevel(@PathVariable String levelId, @RequestBody SoundLevel level) {
        return soundService.updateLevel(levelId, level)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String levelId) {
        boolean deleted = soundService.deleteLevel(levelId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}