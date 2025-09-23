package com.springboot.quizgame.service;

import com.springboot.quizgame.entity.QuizLevel;
import com.springboot.quizgame.entity.QuizUserProgress;
import com.springboot.quizgame.repositories.QuizLevelRepository;
import com.springboot.quizgame.repositories.QuizUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private QuizLevelRepository levelRepository;
    private QuizUserProgressRepository progressRepository;

    @Autowired
    public QuizService(QuizLevelRepository levelRepository, QuizUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<QuizLevel> getQuizByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }

    public List<QuizLevel> getAllLevels() {
        return levelRepository.findAll();
    }

    public QuizUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<QuizUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);

        QuizUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new QuizUserProgress(username, levelId, isCompleted);
            progress.setTimeSpent(timeSpent);
        }

        if (isCompleted) {
            progress.setCompletionDate(LocalDateTime.now());
        }

        return progressRepository.save(progress);
    }

    public boolean getLevelCompletion(String username, String levelId) {
        return progressRepository.findByUsernameAndLevelId(username, levelId)
                .map(QuizUserProgress::getCompleted)
                .orElse(false);
    }

    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<QuizUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        QuizUserProgress::getLevelId,
                        QuizUserProgress::getCompleted
                ));
    }

    public Map<String, Object> getUserStatistics(String username) {
        List<QuizUserProgress> completedLevels = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<QuizUserProgress> allProgress = progressRepository.findByUsername(username);

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

    public QuizLevel createLevel(QuizLevel level) {
        return levelRepository.save(level);
    }

    public Optional<QuizLevel> updateLevel(String levelId, QuizLevel updatedLevel) {
        return levelRepository.findByLevelId(levelId)
                .map(existing -> {
                    existing.setTitle(updatedLevel.getTitle());
                    existing.setDifficulty(updatedLevel.getDifficulty());
                    existing.setQuestions(updatedLevel.getQuestions());
                    existing.setQuestionCount(updatedLevel.getQuestions().size());
                    return levelRepository.save(existing);
                });
    }

    public boolean deleteLevel(String levelId) {
        return levelRepository.findByLevelId(levelId)
                .map(level -> {
                    levelRepository.delete(level);
                    return true;
                })
                .orElse(false);
    }
}