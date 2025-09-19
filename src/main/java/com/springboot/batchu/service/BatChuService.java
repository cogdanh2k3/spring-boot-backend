package com.springboot.batchu.service;

import com.springboot.batchu.entity.batChuLevel;
import com.springboot.batchu.entity.batChuUserProgress;
import com.springboot.batchu.repositories.BatChuLevelRepository;
import com.springboot.batchu.repositories.BatChuUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BatChuService {

    private BatChuLevelRepository levelRepository;
    private BatChuUserProgressRepository progressRepository;

    @Autowired
    public BatChuService(BatChuLevelRepository levelRepository, BatChuUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<batChuLevel> getBatChuByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public List<batChuLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    // Save level completion
    public batChuUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<batChuUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);

        batChuUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new batChuUserProgress(username, levelId, isCompleted);
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
                .map(batChuUserProgress::getCompleted)
                .orElse(false);
    }

    // Get all level completions
    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<batChuUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        batChuUserProgress::getLevelId,
                        batChuUserProgress::getCompleted
                ));
    }

    // Get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<batChuUserProgress> completedLevels = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<batChuUserProgress> allProgress = progressRepository.findByUsername(username);

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
    public batChuLevel createLevel(batChuLevel level) {
        return levelRepository.save(level);
    }

    // Update existing level
    public Optional<batChuLevel> updateLevel(String levelId, batChuLevel updatedLevel) {
        return levelRepository.findByLevelId(levelId)
                .map(existing -> {
                    existing.setTitle(updatedLevel.getTitle());
                    existing.setDifficulty(updatedLevel.getDifficulty());
                    existing.setQuestions(updatedLevel.getQuestions());
                    existing.setQuestionCount(updatedLevel.getQuestions().size());
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