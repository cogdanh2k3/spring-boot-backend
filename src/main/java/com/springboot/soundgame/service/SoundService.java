package com.springboot.soundgame.service;

import com.springboot.soundgame.entity.SoundClip;
import com.springboot.soundgame.entity.SoundLevel;
import com.springboot.soundgame.entity.SoundUserProgress;
import com.springboot.soundgame.repository.SoundClipRepository;
import com.springboot.soundgame.repository.SoundLevelRepository;
import com.springboot.soundgame.repository.SoundUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SoundService {

    @Autowired
    private SoundLevelRepository levelRepository;

    @Autowired
    private SoundClipRepository clipRepository;

    @Autowired
    private SoundUserProgressRepository progressRepository;

    // ===== LEVEL OPERATIONS =====

    public List<SoundLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    public Optional<SoundLevel> getLevelById(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public Optional<SoundClip> getRandomClipFromLevel(String levelId) {
        Optional<SoundLevel> lvl = levelRepository.findByLevelId(levelId);
        if (lvl.isEmpty()) return Optional.empty();
        List<SoundClip> clips = lvl.get().getClips();
        if (clips == null || clips.isEmpty()) return Optional.empty();
        Random rand = new Random();
        return Optional.of(clips.get(rand.nextInt(clips.size())));
    }

    public boolean checkAnswer(String clipId, String answer) {
        Optional<SoundClip> clipOpt = clipRepository.findByClipId(clipId);
        if (clipOpt.isEmpty()) return false;
        String expected = clipOpt.get().getAnswer();
        return expected != null && expected.equalsIgnoreCase(answer == null ? "" : answer.trim());
    }

    // ===== USER PROGRESS OPERATIONS (NEW) =====

    public SoundUserProgress saveUserProgress(SoundUserProgress progress) {
        // Check if progress already exists for this user and level
        Optional<SoundUserProgress> existing = progressRepository
                .findByUsernameAndLevelId(progress.getUsername(), progress.getLevelId());

        if (existing.isPresent()) {
            // Update existing progress
            SoundUserProgress existingProgress = existing.get();
            existingProgress.setCompleted(progress.getCompleted());
            existingProgress.setTimeSpent(progress.getTimeSpent());
            return progressRepository.save(existingProgress);
        } else {
            // Create new progress record
            return progressRepository.save(progress);
        }
    }

    public Optional<SoundUserProgress> getUserProgress(String username, String levelId) {
        return progressRepository.findByUsernameAndLevelId(username, levelId);
    }

    public List<SoundUserProgress> getAllUserProgress(String username) {
        return progressRepository.findByUsername(username);
    }

    public List<SoundUserProgress> getCompletedLevels(String username) {
        return progressRepository.findByUsernameAndCompleted(username, true);
    }

    // ===== ADMIN CRUD FOR LEVELS =====

    public SoundLevel createLevel(SoundLevel level) {
        return levelRepository.save(level);
    }

    public Optional<SoundLevel> updateLevel(String levelId, SoundLevel updated) {
        return levelRepository.findByLevelId(levelId).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDifficulty(updated.getDifficulty());
            existing.setClips(updated.getClips());
            existing.setQuestionCount(existing.getClips() != null ? existing.getClips().size() : 0);
            return levelRepository.save(existing);
        });
    }

    public boolean deleteLevel(String levelId) {
        Optional<SoundLevel> lvl = levelRepository.findByLevelId(levelId);
        if (lvl.isPresent()) {
            levelRepository.deleteById(lvl.get().getId());
            return true;
        }
        return false;
    }
}