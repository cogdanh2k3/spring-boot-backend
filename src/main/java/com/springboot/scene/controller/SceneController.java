package com.springboot.scene.controller;
import com.springboot.scene.entity.SceneLevel;
import com.springboot.scene.entity.SceneLocation;
import com.springboot.scene.entity.SceneUserProgress;
import com.springboot.scene.service.SceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scene")
@CrossOrigin(origins = "*")
public class SceneController {
    private final SceneService sceneService;
    @Autowired
    public SceneController(SceneService sceneService) {
        this.sceneService = sceneService;
    }
    // Get level data
    @GetMapping("/levels/{levelId}")
    public ResponseEntity<SceneLevelResponse> getSceneLevel(@PathVariable String levelId) {
        return sceneService.getSceneByLevel(levelId)
                .map(level -> {
                    SceneLevelResponse response = new SceneLevelResponse(
                            level.getLevelId(),
                            level.getTitle(),
                            level.getDifficulty(),
                            level.getQuestionCount(),
                            level.getLocations()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // Get all available levels
    @GetMapping("/levels")
    public ResponseEntity<List<SceneLevel>> getAllLevels() {
        List<SceneLevel> levels = sceneService.getAllLevels();
        return ResponseEntity.ok(levels);
    }
    // Save level completion
    @PostMapping("/progress")
    public ResponseEntity<SceneUserProgress> saveLevelCompletion(@RequestBody CompletionRequest request) {
        SceneUserProgress progress = sceneService.saveLevelCompletion(
                request.getUserName(),
                request.getLevelId(),
                request.isCompleted(),
                request.getTimeSpent()
        );
        return ResponseEntity.ok(progress);
    }
    // Get level completion status
    @GetMapping("/progress/{userName}/{levelId}")
    public ResponseEntity<CompletionResponse> getLevelCompletion(
            @PathVariable String userName,
            @PathVariable String levelId) {
        boolean isCompleted = sceneService.getLevelCompletion(userName, levelId);
        return ResponseEntity.ok(new CompletionResponse(isCompleted));
    }
    // Get all level completions for a user
    @GetMapping("/progress/{userName}")
    public ResponseEntity<Map<String, Boolean>> getAllLevelCompletion(@PathVariable String userName) {
        Map<String, Boolean> completions = sceneService.getAllLevelCompletions(userName);
        return ResponseEntity.ok(completions);
    }
    // Get user statistics
    @GetMapping("/users/{userName}/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String userName) {
        Map<String, Object> stats = sceneService.getUserStatistics(userName);
        return ResponseEntity.ok(stats);
    }
    // Create new level
    @PostMapping("/admin/levels")
    public ResponseEntity<SceneLevel> createLevel(@RequestBody SceneLevel level) {
        SceneLevel createdLevel = sceneService.createLevel(level);
        return ResponseEntity.ok(createdLevel);
    }
    // Update existing level
    @PutMapping("/admin/levels/{levelId}")
    public ResponseEntity<SceneLevel> updateLevel(@PathVariable String levelId, @RequestBody SceneLevel level) {
        return sceneService.updateLevel(levelId, level)
                .map(updatedLevel -> ResponseEntity.ok(updatedLevel))
                .orElse(ResponseEntity.notFound().build());
    }
    // Delete level
    @DeleteMapping("/admin/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String levelId) {
        boolean deleted = sceneService.deleteLevel(levelId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "scene-backend"));
    }
    // Guess API
    @PostMapping("/guess")
    public ResponseEntity<Map<String, Double>> guessLocation(@RequestBody GuessRequest request) {
        double distance = sceneService.calculateDistance(request.getImageId(), request.getGuessLat(), request.getGuessLon());
        return ResponseEntity.ok(Map.of("distance", distance));
    }
    // DTOs
    public static class CompletionRequest {
        private String userName;
        private String levelId;
        private boolean completed;
        private String timeSpent;
        // Getters and Setters
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getLevelId() { return levelId; }
        public void setLevelId(String levelId) { this.levelId = levelId; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public String getTimeSpent() { return timeSpent; }
        public void setTimeSpent(String timeSpent) { this.timeSpent = timeSpent; }
    }
    public static class CompletionResponse {
        private boolean completed;
        public CompletionResponse(boolean completed) {
            this.completed = completed;
        }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
    public static class SceneLevelResponse {
        private String levelId;
        private String title;
        private String difficulty;
        private Integer questionCount;
        private List<SceneLocation> locations;
        public SceneLevelResponse(String levelId, String title, String difficulty,
                                  Integer questionCount, List<SceneLocation> locations) {
            this.levelId = levelId;
            this.title = title;
            this.difficulty = difficulty;
            this.questionCount = questionCount;
            this.locations = locations;
        }
        // Getters and Setters
        public String getLevelId() { return levelId; }
        public void setLevelId(String levelId) { this.levelId = levelId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
        public Integer getQuestionCount() { return questionCount; }
        public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
        public List<SceneLocation> getLocations() { return locations; }
        public void setLocations(List<SceneLocation> locations) { this.locations = locations; }
    }
    public static class GuessRequest {
        private double guessLat;
        private double guessLon;
        private String imageId;
        // Getters and Setters
        public double getGuessLat() { return guessLat; }
        public void setGuessLat(double guessLat) { this.guessLat = guessLat; }
        public double getGuessLon() { return guessLon; }
        public void setGuessLon(double guessLon) { this.guessLon = guessLon; }
        public String getImageId() { return imageId; }
        public void setImageId(String imageId) { this.imageId = imageId; }
    }
}