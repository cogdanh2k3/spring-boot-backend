package com.springboot.service;

import com.springboot.entity.WordSearchTopic;
import com.springboot.repositories.WordSearchTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class DataInitializationService implements CommandLineRunner {
    @Autowired
    private WordSearchTopicRepository topicRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultTopics();
    }

    private void initializeDefaultTopics() {

        //check data already exits?
        if(topicRepository.count() > 0){
            System.out.println("Topics have been initialized, skipping");
            return;
        }

        System.out.println("Initializing default topics...");

        // Travel
        List<String> travelWords = Arrays.asList(
                "AIRPLANE", "HOTEL", "PASSPORT", "LUGGAGE", "VACATION",
                "BEACH", "MOUNTAIN", "TOURIST", "JOURNEY", "EXPLORE"
        );

        WordSearchTopic travelTopic = new WordSearchTopic(
                "Travel", "Travel & Adventure", "Medium", 10, travelWords
        );

        // fun & game
        List<String> funGamesWords = Arrays.asList(
                "PUZZLE", "GAME", "FUN", "PLAY", "ARCADE",
                "SPORT", "TENNIS", "SOCCER", "BOWLING", "CHESS"
        );
        WordSearchTopic funGamesTopic = new WordSearchTopic(
                "FunAndGames", "Fun & Games", "Easy", 8, funGamesWords
        );

        // study & work
        List<String> studyWorkWords = Arrays.asList(
                "STUDY", "WORK", "OFFICE", "COMPUTER", "KEYBOARD",
                "MEETING", "PROJECT", "DEADLINE", "REPORT", "ANALYSIS"
        );
        WordSearchTopic studyWorkTopic = new WordSearchTopic(
                "StudyWork", "Study & Work", "Hard", 12, studyWorkWords
        );

        //technology
        List<String> techWords = Arrays.asList(
                "ANDROID", "KOTLIN", "JAVA", "SPRING", "DATABASE",
                "API", "BACKEND", "FRONTEND", "MOBILE", "APP"
        );
        WordSearchTopic techTopic = new WordSearchTopic(
                "Technology", "Technology", "Expert", 10, techWords
        );

        //save topics
        topicRepository.saveAll(Arrays.asList(travelTopic, funGamesTopic, studyWorkTopic, techTopic));

        System.out.println("Successfully initialized " + topicRepository.count() + " topics");
    }
}
