package com.springboot.service;

import com.springboot.entity.WordSearchTopic;
import com.springboot.repositories.WordSearchTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component  // Added this annotation
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private WordSearchTopicRepository topicRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultTopics();
    }

    private void initializeDefaultTopics() {
        // Check if data already exists?
        if (topicRepository.count() > 0) {
            System.out.println("Topics have been initialized, skipping");
            return;
        }

        System.out.println("Initializing default topics...");

        // Travel Topic
        List<String> travelWords = Arrays.asList(
                "AIRPLANE", "HOTEL", "PASSPORT", "LUGGAGE", "VACATION",
                "BEACH", "MOUNTAIN", "TOURIST", "JOURNEY", "EXPLORE"
        );
        WordSearchTopic travelTopic = new WordSearchTopic(
                "Travel", "Travel & Adventure", "Easy", 12, 10, travelWords
        );
        travelTopic.setWordCount(travelWords.size());

        // Fun & Games Topic
        List<String> funGamesWords = Arrays.asList(
                "PUZZLE", "GAME", "FUN", "PLAY", "ARCADE",
                "SPORT", "TENNIS", "SOCCER", "BOWLING", "CHESS"
        );
        WordSearchTopic funGamesTopic = new WordSearchTopic(
                "FunAndGames", "Fun & Games", "Easy", 12, 10, funGamesWords
        );
        funGamesTopic.setWordCount(funGamesWords.size());

        // Study & Work Topic
        List<String> studyWorkWords = Arrays.asList(
                "STUDY", "WORK", "OFFICE", "COMPUTER", "KEYBOARD",
                "MEETING", "PROJECT", "DEADLINE", "REPORT", "ANALYSIS"
        );
        WordSearchTopic studyWorkTopic = new WordSearchTopic(
                "StudyWork", "Study & Work", "Easy", 12, 10, studyWorkWords
        );
        studyWorkTopic.setWordCount(studyWorkWords.size());

        // Technology Topic
        List<String> techWords = Arrays.asList(
                "ANDROID", "KOTLIN", "JAVA", "SPRING", "DATABASE",
                "API", "BACKEND", "FRONTEND", "MOBILE", "APP"
        );
        WordSearchTopic techTopic = new WordSearchTopic(
                "Technology", "Technology", "Medium", 12, 10, techWords
        );
        techTopic.setWordCount(techWords.size());

        // Save topics
        topicRepository.saveAll(Arrays.asList(travelTopic, funGamesTopic, studyWorkTopic, techTopic));

        System.out.println("Successfully initialized " + topicRepository.count() + " topics");
    }
}