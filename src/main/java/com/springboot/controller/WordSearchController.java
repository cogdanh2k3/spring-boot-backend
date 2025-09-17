package com.springboot.controller;

import com.springboot.entity.WordSearchTopic;
import com.springboot.entity.UserProgress;
import com.springboot.service.WordSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wordsearch")
@CrossOrigin(origins = "*")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    @Autowired
    public WordSearchController(WordSearchService wordSearchService) {
        this.wordSearchService = wordSearchService;
    }

    // Get word data
    @GetMapping("/topics/{topicId}")
    public ResponseEntity<WordSearchTopicResponse> getWordSearchTopic(@PathVariable String topicId) {
        return wordSearchService.getWordSearchByTopic(topicId)
                .map(topic -> {
                    WordSearchTopicResponse response = new WordSearchTopicResponse(
                            topic.getTopicId(),
                            topic.getTitle(),
                            topic.getDifficulty(),
                            topic.getGridSize(),
                            topic.getWordCount(),
                            topic.getWords()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all available topics
    @GetMapping("/topics")
    public ResponseEntity<List<WordSearchTopic>> getAllTopics() {
        List<WordSearchTopic> topics = wordSearchService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    // Save topic completion
    @PostMapping("/progress")
    public ResponseEntity<UserProgress> saveTopicCompletion(@RequestBody CompletionRequest request) {
        UserProgress progress = wordSearchService.saveTopicCompletion(
                request.getUserName(),
                request.getTopicId(),
                request.isCompleted(),
                request.getTimeSpent()
        );
        return ResponseEntity.ok(progress);
    }

    // Get topic completion status
    @GetMapping("/progress/{userName}/{topicId}")
    public ResponseEntity<CompletionResponse> getTopicCompletion(
            @PathVariable String userName,
            @PathVariable String topicId) {
        boolean isCompleted = wordSearchService.getTopicCompletion(userName, topicId);
        return ResponseEntity.ok(new CompletionResponse(isCompleted));
    }

    // Get all topic completions for a user
    @GetMapping("/progress/{userName}")
    public ResponseEntity<Map<String, Boolean>> getAllTopicCompletion(@PathVariable String userName) {
        Map<String, Boolean> completions = wordSearchService.getAllTopicCompletions(userName);
        return ResponseEntity.ok(completions);
    }

    // Get user statistics (Fixed the mapping path)
    @GetMapping("/users/{userName}/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String userName) {
        Map<String, Object> stats = wordSearchService.getUserStatistics(userName);
        return ResponseEntity.ok(stats);
    }

    // Create new topic
    @PostMapping("/admin/topics")
    public ResponseEntity<WordSearchTopic> createTopic(@RequestBody WordSearchTopic topic) {
        WordSearchTopic createdTopic = wordSearchService.createTopic(topic);
        return ResponseEntity.ok(createdTopic);
    }

    // Update existing topic
    @PutMapping("/admin/topics/{topicId}")
    public ResponseEntity<WordSearchTopic> updateTopic(@PathVariable String topicId, @RequestBody WordSearchTopic topic) {
        return wordSearchService.updateTopic(topicId, topic)
                .map(updatedTopic -> ResponseEntity.ok(updatedTopic))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete topic
    @DeleteMapping("/admin/topics/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable String topicId) {
        boolean deleted = wordSearchService.deleteTopic(topicId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "word-search-backend"));
    }

    // DTOs
    public static class CompletionRequest {
        private String userName;
        private String topicId;
        private boolean completed;
        private String timeSpent;

        // Constructors
        public CompletionRequest() {}

        public CompletionRequest(String userName, String topicId, boolean completed, String timeSpent) {
            this.userName = userName;
            this.topicId = topicId;
            this.completed = completed;
            this.timeSpent = timeSpent;
        }

        // Getters and Setters
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getTopicId() { return topicId; }
        public void setTopicId(String topicId) { this.topicId = topicId; }

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

    public static class WordSearchTopicResponse {
        private String topicId;
        private String title;
        private String difficulty;
        private Integer gridSize;
        private Integer wordCount;
        private List<String> words;

        public WordSearchTopicResponse() {}

        public WordSearchTopicResponse(String topicId, String title, String difficulty,
                                       Integer gridSize, Integer wordCount, List<String> words) {
            this.topicId = topicId;
            this.title = title;
            this.difficulty = difficulty;
            this.gridSize = gridSize;
            this.wordCount = wordCount;
            this.words = words;
        }

        // Getters and Setters
        public String getTopicId() { return topicId; }
        public void setTopicId(String topicId) { this.topicId = topicId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDifficulty() { return difficulty; }
        public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

        public Integer getGridSize() { return gridSize; }
        public void setGridSize(Integer gridSize) { this.gridSize = gridSize; }

        public Integer getWordCount() { return wordCount; }
        public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }

        public List<String> getWords() { return words; }
        public void setWords(List<String> words) { this.words = words; }
    }
}