package com.springboot.matchgame.service;

import com.springboot.matchgame.entity.MatchGameLevel;
import com.springboot.matchgame.entity.MatchGameUserProgress;
import com.springboot.matchgame.repositories.MatchGameLevelRepository;
import com.springboot.matchgame.repositories.MatchGameUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchGameService {

    private MatchGameLevelRepository levelRepository;
    private MatchGameUserProgressRepository progressRepository;

    @Autowired
    public MatchGameService(MatchGameLevelRepository levelRepository, MatchGameUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<MatchGameLevel> getMatchGameByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public List<MatchGameLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    // Save level completion
    public MatchGameUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<MatchGameUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);

        MatchGameUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new MatchGameUserProgress(username, levelId, isCompleted);
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
                .map(MatchGameUserProgress::getCompleted)
                .orElse(false);
    }

    // Get all level completions
    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<MatchGameUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        MatchGameUserProgress::getLevelId,
                        MatchGameUserProgress::getCompleted
                ));
    }

    // Get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<MatchGameUserProgress> completedLevels = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<MatchGameUserProgress> allProgress = progressRepository.findByUsername(username);

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
    public MatchGameLevel createLevel(MatchGameLevel level) {
        return levelRepository.save(level);
    }

    // Update existing level
    public Optional<MatchGameLevel> updateLevel(String levelId, MatchGameLevel updatedLevel) {
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