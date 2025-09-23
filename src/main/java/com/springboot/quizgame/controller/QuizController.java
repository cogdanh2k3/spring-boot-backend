package com.springboot.quizgame.controller;

import com.springboot.quizgame.entity.QuizLevel;
import com.springboot.quizgame.entity.QuizQuestion;
import com.springboot.quizgame.entity.QuizUserProgress;
import com.springboot.quizgame.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/levels/{levelId}")
    public ResponseEntity<QuizLevelResponse> getQuizLevel(@PathVariable String levelId) {
        return quizService.getQuizByLevel(levelId)
                .map(level -> {
                    QuizLevelResponse response = new QuizLevelResponse(
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

    @GetMapping("/levels")
    public ResponseEntity<List<QuizLevel>> getAllLevels() {
        List<QuizLevel> levels = quizService.getAllLevels();
        return ResponseEntity.ok(levels);
    }

    @PostMapping("/progress")
    public ResponseEntity<QuizUserProgress> saveLevelCompletion(@RequestBody CompletionRequest request) {
        QuizUserProgress progress = quizService.saveLevelCompletion(
                request.getUserName(),
                request.getLevelId(),
                request.isCompleted(),
                request.getTimeSpent()
        );
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/progress/{userName}/{levelId}")
    public ResponseEntity<CompletionResponse> getLevelCompletion(
            @PathVariable String userName,
            @PathVariable String levelId) {
        boolean isCompleted = quizService.getLevelCompletion(userName, levelId);
        return ResponseEntity.ok(new CompletionResponse(isCompleted));
    }

    @GetMapping("/progress/{userName}")
    public ResponseEntity<Map<String, Boolean>> getAllLevelCompletion(@PathVariable String userName) {
        Map<String, Boolean> completions = quizService.getAllLevelCompletions(userName);
        return ResponseEntity.ok(completions);
    }

    @GetMapping("/users/{userName}/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String userName) {
        Map<String, Object> stats = quizService.getUserStatistics(userName);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/admin/levels")
    public ResponseEntity<QuizLevel> createLevel(@RequestBody QuizLevel level) {
        QuizLevel createdLevel = quizService.createLevel(level);
        return ResponseEntity.ok(createdLevel);
    }

    @PutMapping("/admin/levels/{levelId}")
    public ResponseEntity<QuizLevel> updateLevel(@PathVariable String levelId, @RequestBody QuizLevel level) {
        return quizService.updateLevel(levelId, level)
                .map(updatedLevel -> ResponseEntity.ok(updatedLevel))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String levelId) {
        boolean deleted = quizService.deleteLevel(levelId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
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

    public static class QuizLevelResponse {
        private String levelId;
        private String title;
        private String difficulty;
        private Integer questionCount;
        private List<QuizQuestion> questions;

        public QuizLevelResponse() {}

        public QuizLevelResponse(String levelId, String title, String difficulty,
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