package com.springboot.quizgame.service;

import com.springboot.quizgame.entity.QuizGameLevel;
import com.springboot.quizgame.entity.QuizQuestion;
import com.springboot.quizgame.repositories.QuizGameLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class QuizGameDataInitializationService implements CommandLineRunner {

    @Autowired
    private QuizGameLevelRepository levelRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLevels();
    }

    private void initializeDefaultLevels() {
        // Check if data already exists
        if (levelRepository.count() > 0) {
            System.out.println("Levels have been initialized, skipping");
            return;
        }

        System.out.println("Initializing default levels...");

        // Easy Level
        List<QuizQuestion> easyQuestions = Arrays.asList(
                new QuizQuestion(
                        "Leave the driving to us! was a phrase advertising which company?",
                        "Greyhound",
                        "world",
                        Arrays.asList("Pan Am Airlines", "Amtrak", "Greyhound", "Hertz")
                )
                // Add more questions as needed, e.g., question_101, question_102, etc.
        );
        QuizGameLevel easyLevel = new QuizGameLevel(
                "LevelEasy", "Easy Level", "Easy", easyQuestions
        );

        // Normal Level
        List<QuizQuestion> normalQuestions = Arrays.asList(
                new QuizQuestion(
                        "What is the capital of France?",
                        "Paris",
                        "geography",
                        Arrays.asList("London", "Berlin", "Paris", "Madrid")
                )
                // Add more questions as needed
        );
        QuizGameLevel normalLevel = new QuizGameLevel(
                "LevelNormal", "Normal Level", "Normal", normalQuestions
        );

        // Hard Level
        List<QuizQuestion> hardQuestions = Arrays.asList(
                new QuizQuestion(
                        "Who developed the theory of relativity?",
                        "Albert Einstein",
                        "science",
                        Arrays.asList("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Nikola Tesla")
                )
                // Add more questions as needed
        );
        QuizGameLevel hardLevel = new QuizGameLevel(
                "LevelHard", "Hard Level", "Hard", hardQuestions
        );

        // Save levels
        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}