package com.springboot.matchgame.service;

import com.springboot.matchgame.entity.MatchLevel;
import com.springboot.matchgame.entity.MatchUserProgress;
import com.springboot.matchgame.repositories.MatchLevelRepository;
import com.springboot.matchgame.repositories.MatchUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private MatchLevelRepository levelRepository;
    private MatchUserProgressRepository progressRepository;

    @Autowired
    public MatchService(MatchLevelRepository levelRepository, MatchUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<MatchLevel> getMatchByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public List<MatchLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    // Save level completion
    public MatchUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<MatchUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);

        MatchUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new MatchUserProgress(username, levelId, isCompleted);
            progress.setTimeSpent(timeSpent);
        }

        if (isCompleted) {
            progress.setCompletionDate(LocalDateTime.now());
        }

        return progressRepository.save(progress);
    }

    // Get level completion status for a user
    public boolean getLevelCompletion(String username, String levelId) {
        return progressRepository.findByUsernameAndLevelId(username, levelId)
                .map(MatchUserProgress::getCompleted)
                .orElse(false);
    }

    // Get all level completions
    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<MatchUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        MatchUserProgress::getLevelId,
                        MatchUserProgress::getCompleted
                ));
    }

    // Get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<MatchUserProgress> completedLevels = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<MatchUserProgress> allProgress = progressRepository.findByUsername(username);

        return Map.of(
                "completedLevels", completedLevels.size(),
                "totalLevels", levelRepository.count(),
                "completionRate", allProgress.isEmpty() ? 0.0 : (double) completedLevels.size() / levelRepository.count() * 100,
                "recentCompletions", completedLevels.stream()
                        .filter(p -> p.getCompletionDate() != null)
                        .sorted((a, b) -> b.getCompletionDate().compareTo(a.getCompletionDate()))
                        .limit(5)
                        .collect(Collectors.toList())
        );
    }

    // Create a new level
    public MatchLevel createLevel(MatchLevel level) {
        return levelRepository.save(level);
    }

    // Update existing level
    public Optional<MatchLevel> updateLevel(String levelId, MatchLevel updatedLevel) {
        return levelRepository.findByLevelId(levelId)
                .map(existing -> {
                    existing.setTitle(updatedLevel.getTitle());
                    existing.setDifficulty(updatedLevel.getDifficulty());
                    existing.setPairs(updatedLevel.getPairs());
                    existing.setPairCount(updatedLevel.getPairs().size());
                    return levelRepository.save(existing);
                });
    }

    // Delete level
    public boolean deleteLevel(String levelId) {
        return levelRepository.findByLevelId(levelId)
                .map(level -> {
                    levelRepository.delete(level);
                    return true;
                })
                .orElse(false);
    }
}