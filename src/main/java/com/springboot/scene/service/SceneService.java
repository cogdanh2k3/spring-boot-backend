package com.springboot.scene.service;
import com.springboot.scene.entity.SceneLevel;
import com.springboot.scene.entity.SceneLocation;
import com.springboot.scene.entity.SceneUserProgress;
import com.springboot.scene.repositories.SceneLevelRepository;
import com.springboot.scene.repositories.SceneLocationRepository;
import com.springboot.scene.repositories.SceneUserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SceneService {
    private final SceneLevelRepository levelRepository;
    private final SceneLocationRepository locationRepository;
    private final SceneUserProgressRepository progressRepository;
    @Autowired
    public SceneService(SceneLevelRepository levelRepository, SceneLocationRepository locationRepository, SceneUserProgressRepository progressRepository) {
        this.levelRepository = levelRepository;
        this.locationRepository = locationRepository;
        this.progressRepository = progressRepository;
    }
    public Optional<SceneLevel> getSceneByLevel(String levelId) {
        return levelRepository.findByLevelId(levelId);
    }
    public List<SceneLevel> getAllLevels() {
        return levelRepository.findAll();
    }
    // Save level completion
    public SceneUserProgress saveLevelCompletion(String username, String levelId, boolean isCompleted, String timeSpent) {
        Optional<SceneUserProgress> existingProgress = progressRepository.findByUsernameAndLevelId(username, levelId);
        SceneUserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        } else {
            progress = new SceneUserProgress(username, levelId, isCompleted);
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
                .map(SceneUserProgress::getCompleted)
                .orElse(false);
    }
    // Get all level completions
    public Map<String, Boolean> getAllLevelCompletions(String username) {
        List<SceneUserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        SceneUserProgress::getLevelId,
                        SceneUserProgress::getCompleted
                ));
    }
    // Get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<SceneUserProgress> completedLevels = progressRepository.findByUsernameAndCompleted(username, true);
        List<SceneUserProgress> allProgress = progressRepository.findByUsername(username);
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
    public SceneLevel createLevel(SceneLevel level) {
        return levelRepository.save(level);
    }
    // Update existing level
    public Optional<SceneLevel> updateLevel(String levelId, SceneLevel updatedLevel) {
        return levelRepository.findByLevelId(levelId)
                .map(existing -> {
                    existing.setTitle(updatedLevel.getTitle());
                    existing.setDifficulty(updatedLevel.getDifficulty());
                    existing.setLocations(updatedLevel.getLocations());
                    existing.setQuestionCount(updatedLevel.getLocations().size());
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
    // Calculate distance for guess
    public double calculateDistance(String locationId, double guessLat, double guessLon) {
        Optional<SceneLocation> locationOpt = locationRepository.findByLocationId(locationId);
        if (locationOpt.isEmpty()) {
            throw new RuntimeException("Location not found");
        }
        SceneLocation location = locationOpt.get();
        return haversine(guessLat, guessLon, location.getTrueLat(), location.getTrueLon());
    }
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}