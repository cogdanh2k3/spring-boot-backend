package com.springboot.quizgame.controller;

import com.springboot.quizgame.entity.QuizGameLevel;
import com.springboot.quizgame.entity.QuizGameUserProgress;
import com.springboot.quizgame.entity.QuizQuestion;
import com.springboot.quizgame.service.QuizGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizgame")
@CrossOrigin(origins = "*")
public class QuizGameController {

    private final QuizGameService quizGameService;

    @Autowired
    public QuizGameController(QuizGameService quizGameService) {
        this.quizGameService = quizGameService;
    }

    // Get level data
    @GetMapping("/levels/{levelId}")
    public ResponseEntity<QuizGameLevelResponse> getQuizGameLevel(@PathVariable String levelId) {
        return quizGameService.getQuizGameByLevel(levelId)
                .map(level -> {
                    QuizGameLevelResponse response = new QuizGameLevelResponse(
                            level.getLevelId(),
                            level.getTitle(),
                            level.getDifficulty(),
                            level.getQuestionCount(),
                            level.getQuestions()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all available levels
    @GetMapping("/levels")
    public ResponseEntity<List<QuizGameLevel>> getAllLevels() {
        List<QuizGameLevel> levels = quizGameService.getAllLevels();
        return ResponseEntity.ok(levels);
    }

    // Save level completion
    @PostMapping("/progress")
    public ResponseEntity<QuizGameUserProgress> saveLevelCompletion(@RequestBody CompletionRequest request) {
        QuizGameUserProgress progress = quizGameService.saveLevelCompletion(
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
        boolean isCompleted = quizGameService.getLevelCompletion(userName, levelId);
        return ResponseEntity.ok(new CompletionResponse(isCompleted));
    }

    // Get all level completions for a user
    @GetMapping("/progress/{userName}")
    public ResponseEntity<Map<String, Boolean>> getAllLevelCompletion(@PathVariable String userName) {
        Map<String, Boolean> completions = quizGameService.getAllLevelCompletions(userName);
        return ResponseEntity.ok(completions);
    }

    // Get user statistics
    @GetMapping("/users/{userName}/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String userName) {
        Map<String, Object> stats = quizGameService.getUserStatistics(userName);
        return ResponseEntity.ok(stats);
    }

    // Create new level
    @PostMapping("/admin/levels")
    public ResponseEntity<QuizGameLevel> createLevel(@RequestBody QuizGameLevel level) {
        QuizGameLevel createdLevel = quizGameService.createLevel(level);
        return ResponseEntity.ok(createdLevel);
    }

    // Update existing level
    @PutMapping("/admin/levels/{levelId}")
    public ResponseEntity<QuizGameLevel> updateLevel(@PathVariable String levelId, @RequestBody QuizGameLevel level) {
        return quizGameService.updateLevel(levelId, level)
                .map(updatedLevel -> ResponseEntity.ok(updatedLevel))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete level
    @DeleteMapping("/admin/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String levelId) {
        boolean deleted = quizGameService.deleteLevel(levelId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "quizgame-backend"));
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

    public static class QuizGameLevelResponse {
        private String levelId;
        private String title;
        private String difficulty;
        private Integer questionCount;
        private List<QuizQuestion> questions;

        public QuizGameLevelResponse() {}

        public QuizGameLevelResponse(String levelId, String title, String difficulty,
                                     Integer questionCount, List<QuizQuestion> questions) {
            this.levelId = levelId;
            this.title = title;
            this.difficulty = difficulty;
            this.questionCount = questionCount;
            this.questions = questions;
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

        public List<QuizQuestion> getQuestions() { return questions; }
        public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }
    }
}