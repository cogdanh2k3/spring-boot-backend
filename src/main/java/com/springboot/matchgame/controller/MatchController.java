package com.springboot.matchgame.controller;

import com.springboot.matchgame.entity.MatchLevel;
import com.springboot.matchgame.entity.MatchUserProgress;
import com.springboot.matchgame.entity.MatchWordPair;
import com.springboot.matchgame.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matchgame")
@CrossOrigin(origins = "*")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // Get level data
    @GetMapping("/levels/{levelId}")
    public ResponseEntity<MatchLevelResponse> getMatchLevel(@PathVariable String levelId) {
        return matchService.getMatchByLevel(levelId)
                .map(level -> {
                    MatchLevelResponse response = new MatchLevelResponse(
                            level.getLevelId(),
                            level.getTitle(),
                            level.getDifficulty(),
                            level.getPairCount(),
                            level.getPairs()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all available levels
    @GetMapping("/levels")
    public ResponseEntity<List<MatchLevel>> getAllLevels() {
        List<MatchLevel> levels = matchService.getAllLevels();
        return ResponseEntity.ok(levels);
    }

    // Save level completion
    @PostMapping("/progress")
    public ResponseEntity<MatchUserProgress> saveLevelCompletion(@RequestBody CompletionRequest request) {
        MatchUserProgress progress = matchService.saveLevelCompletion(
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
        boolean isCompleted = matchService.getLevelCompletion(userName, levelId);
        return ResponseEntity.ok(new CompletionResponse(isCompleted));
    }

    // Get all level completions for a user
    @GetMapping("/progress/{userName}")
    public ResponseEntity<Map<String, Boolean>> getAllLevelCompletion(@PathVariable String userName) {
        Map<String, Boolean> completions = matchService.getAllLevelCompletions(userName);
        return ResponseEntity.ok(completions);
    }

    // Get user statistics
    @GetMapping("/users/{userName}/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String userName) {
        Map<String, Object> stats = matchService.getUserStatistics(userName);
        return ResponseEntity.ok(stats);
    }

    // Create new level
    @PostMapping("/admin/levels")
    public ResponseEntity<MatchLevel> createLevel(@RequestBody MatchLevel level) {
        MatchLevel createdLevel = matchService.createLevel(level);
        return ResponseEntity.ok(createdLevel);
    }

    // Update existing level
    @PutMapping("/admin/levels/{levelId}")
    public ResponseEntity<MatchLevel> updateLevel(@PathVariable String levelId, @RequestBody MatchLevel level) {
        return matchService.updateLevel(levelId, level)
                .map(updatedLevel -> ResponseEntity.ok(updatedLevel))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete level
    @DeleteMapping("/admin/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String levelId) {
        boolean deleted = matchService.deleteLevel(levelId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "matchgame-backend"));
    }

    // DTOs
    public static class CompletionRequest {
        private String userName;
        private String levelId;
        private boolean completed;
        private String timeSpent;

        // Constructors
        public CompletionRequest() {}

        public CompletionRequest(String userName, String levelId, boolean completed, String timeSpent) {
            this.userName = userName;
            this.levelId = levelId;
            this.completed = completed;
            this.timeSpent = timeSpent;
        }

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

        public CompletionResponse() {}

        public CompletionResponse(boolean completed) {
            this.completed = completed;
        }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    public static class MatchLevelResponse {
        private String levelId;
        private String title;
        private String difficulty;
        private Integer pairCount;
        private List<MatchWordPair> pairs;

        public MatchLevelResponse() {}

        public MatchLevelResponse(String levelId, String title, String difficulty,
                                  Integer pairCount, List<MatchWordPair> pairs) {
            this.levelId = levelId;
            this.title = title;
            this.difficulty = difficulty;
            this.pairCount = pairCount;
            this.pairs = pairs;
        }

        // Getters and Setters
        public String getLevelId() { return levelId; }
        public void setLevelId(String levelId) { this.levelId = levelId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public Integer getPairCount() { return pairCount; }
        public void setPairCount(Integer pairCount) { this.pairCount = pairCount; }

        public List<MatchWordPair> getPairs() { return pairs; }
        public void setPairs(List<MatchWordPair> pairs) { this.pairs = pairs; }
    }
}