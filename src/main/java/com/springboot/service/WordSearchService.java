package com.springboot.service;

import com.springboot.entity.UserProgress;
import com.springboot.entity.WordSearchTopic;
import com.springboot.repositories.UserProgressRepository;
import com.springboot.repositories.WordSearchTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WordSearchService {

    private WordSearchTopicRepository topicRepository;
    private UserProgressRepository progressRepository;

    @Autowired
    public WordSearchService(WordSearchTopicRepository topicRepository, UserProgressRepository progressRepository) {
        this.topicRepository = topicRepository;
        this.progressRepository = progressRepository;
    }

    public Optional<WordSearchTopic> getWordSearchByTopic(String topicId) {
        return topicRepository.findByTopicId(topicId);
    }

    public List<WordSearchTopic> getAllTopics() {
        return topicRepository.findAll();
    }

    //save topic completion
    public UserProgress saveTopicCompletion(String username, String topicId, boolean isCompleted, String timeSpent) {
        Optional<UserProgress> existingProgress = progressRepository.findByUsernameAndTopicId(username, topicId);

        UserProgress progress;
        if(existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompleted(isCompleted);
            progress.setTimeSpent(timeSpent);
        }else{
            progress = new UserProgress(username, topicId, isCompleted);
            progress.setTimeSpent(timeSpent);
        }

        if(isCompleted) {
            progress.setCompletionDate(LocalDateTime.now());
        }

        return progressRepository.save(progress);
    }

    //get topic completion status for a user
    public boolean getTopicCompletion(String username, String topicId) {
        return progressRepository.findByUsernameAndTopicId(username, topicId)
                .map(UserProgress::getCompleted)
                .orElse(false);
    }

    //get all topic completion
    public Map<String, Boolean> getAllTopicCompletions(String username) {
        List<UserProgress> userProgressList = progressRepository.findByUsername(username);
        return userProgressList.stream()
                .collect(Collectors.toMap(
                        UserProgress::getTopicId,
                        UserProgress::getCompleted
                ));
    }

    //get user statistics
    public Map<String, Object> getUserStatistics(String username) {
        List<UserProgress> completedTopics = progressRepository.findByUsernameAndIsCompleted(username, true);
        List<UserProgress> allProgress = progressRepository.findByUsername(username);

        return Map.of(
                "completedTopics", completedTopics.size(),
                "totalTopic", topicRepository.count(),
                "completionRate", allProgress.isEmpty() ? 0.0 : (double) completedTopics.size() / topicRepository.count() * 100,
                "recentCompletions", completedTopics.stream()
                        .filter(p -> p.getCompletionDate() != null)
                        .sorted((a,b) -> b.getCompletionDate().compareTo(a.getCompletionDate()))
                        .limit(5)
                        .collect(Collectors.toList())
        );
    }

    //create a new topic
    public WordSearchTopic createTopic(WordSearchTopic topic) {
        return topicRepository.save(topic);
    }

    // update existing topic
    public Optional<WordSearchTopic> updateTopic(String topicId, WordSearchTopic updatedTopic) {
        return topicRepository.findByTopicId(topicId)
                .map(existing -> {
                    existing.setTitle(updatedTopic.getTitle());
                    existing.setDifficulty(updatedTopic.getDifficulty());
                    existing.setGridSize(updatedTopic.getGridSize());
                    existing.setWords(updatedTopic.getWords());
                    return topicRepository.save(existing);
                });
    }

    //delete topic
    public boolean deleteTopic(String topicId) {
        return topicRepository.findByTopicId(topicId)
                .map(topic -> {
                    topicRepository.delete(topic);
                    return true;
                })
                .orElse(false);
    }
}
