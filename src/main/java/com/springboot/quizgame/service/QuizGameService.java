package com.springboot.quizgame.service;

import com.springboot.quizgame.entity.QuizGameLevel;
import com.springboot.quizgame.entity.QuizGameUserProgress;
import com.springboot.quizgame.repositories.QuizGameLevelRepository;
import com.springboot.quizgame.repositories.QuizGameUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizGameService {

    private QuizGameLevelRepository levelRepository;
    private QuizGameUserProgressRepository progressRepository;

    @Autowired
    public QuizGameService(QuizGameLevelRepository levelRepository, QuizGameUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<QuizGameLevel> getQuizGameByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public List<QuizGameLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    // Save level completion
    public QuizGameUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<QuizGameUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);

        QuizGameUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new QuizGameUserProgress(username, levelId, isCompleted);
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
                .map(QuizGameUserProgress::getCompleted)
                .orElse(false);
    }

    // Get all level completions
    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<QuizGameUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        QuizGameUserProgress::getLevelId,
                        QuizGameUserProgress::getCompleted
                ));
    }

    // Get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<QuizGameUserProgress> completedLevels = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<QuizGameUserProgress> allProgress = progressRepository.findByUsername(username);

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
    public QuizGameLevel createLevel(QuizGameLevel level) {
        return levelRepository.save(level);
    }

    // Update existing level
    public Optional<QuizGameLevel> updateLevel(String levelId, QuizGameLevel updatedLevel) {
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