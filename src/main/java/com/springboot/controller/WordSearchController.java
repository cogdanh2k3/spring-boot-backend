package com.springboot.controller;

import com.springboot.entity.WordSearchTopic;
import com.springboot.entity.UserProgress;
import com.springboot.service.WordSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/wordsearch")
@CrossOrigin(origins = "*")
public class WordSearchController {

    private WordSearchService wordSearchService;

    @Autowired
    public WordSearchController(WordSearchService wordSearchService) {
        this.wordSearchService = wordSearchService;
    }

    // get word data
    @GetMapping("/topics/{topicId}")
    public ResponseEntity<WordSearchTopic> getWordSearchTopic(@PathVariable String topicId) {
        return wordSearchService.getWordSearchByTopic(topicId)
                .map(topic -> ResponseEntity.ok(topic))
                .orElse(ResponseEntity.notFound().build());
    }

    //get all available topics
    @GetMapping("/topics")
    public ResponseEntity<List<WordSearchTopic>> getAllTopics() {
        List<WordSearchTopic> topics = wordSearchService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    //save topic completion
    @PostMapping("/progress")
    public ResponseEntity<UserProgress> saveTopicCompletion(@RequestBody CompletionRequest request){
        UserProgress progress = wordSearchService.saveTopicCompletion(
                request.getUserName(),
                request.getTopicId(),
                request.isCompleted(),
                request.getTimeSpent()
        );
        return ResponseEntity.ok(progress);
    }

    //get topic completion status
    @GetMapping("/progress/{username}/{topicId}")
    public ResponseEntity<Map<String, Boolean>> getTopicCompletion(@PathVariable String username, @PathVariable String topicId){
        boolean isCompleted = wordSearchService.getTopicCompletion(username, topicId);
        return ResponseEntity.ok(Map.of("isCompleted", isCompleted));
    }

    //get topics for a user
    @GetMapping("/progress/{username}")
    public ResponseEntity<Map<String, Boolean>> getAllTopicCompletion(@PathVariable String username){
        Map<String, Boolean> completions = wordSearchService.getAllTopicCompletions(username);
        return ResponseEntity.ok(completions);
    }

    //Get user statistics
    @GetMapping("/users/username/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable String username){
        Map<String, Object> stats = wordSearchService.getUserStatistics(username);
        return ResponseEntity.ok(stats);
    }

    // creat new topic
    @PostMapping("/admin/topics")
    public ResponseEntity<WordSearchTopic> createTopic(@RequestBody WordSearchTopic topic){
        WordSearchTopic createdTopic = wordSearchService.createTopic(topic);
        return ResponseEntity.ok(createdTopic);
    }

    // update existing topic
    @PutMapping("/admin/topics/{topicId}")
    public ResponseEntity<WordSearchTopic> updateTopic(@PathVariable String topicId, @RequestBody WordSearchTopic topic){
        return wordSearchService.updateTopic(topicId, topic)
                .map(updatedTopic -> ResponseEntity.ok(updatedTopic))
                .orElse(ResponseEntity.notFound().build());
    }

    //delete topic
    @DeleteMapping("/admin/topics/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable String topicId){
        boolean deleted = wordSearchService.deleteTopic(topicId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    //health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "word-search-backend"));
    }

    public static class CompletionRequest {
        private String username;
        private String topicId;
        private boolean completed;
        private String timeSpent;

        // constructors

        public CompletionRequest() {}

        public CompletionRequest(String username, String topicId, boolean completed, String timeSpent) {
            this.username = username;
            this.topicId = topicId;
            this.completed = completed;
            this.timeSpent = timeSpent;
        }

        //Getter and setter

        public String getUserName() {return username;}
        public void setUserName(String username) {this.username = username;}

        public String getTopicId() {return topicId;}
        public void setTopicId(String topicId) {this.topicId = topicId;}

        public boolean isCompleted() {return completed;}
        public void setCompleted(boolean completed) {this.completed = completed;}

        public String getTimeSpent() {return timeSpent;}
        public void setTimeSpent(String timeSpent) {this.timeSpent = timeSpent;}
    }
}
