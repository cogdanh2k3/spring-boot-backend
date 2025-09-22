package com.springboot.matchgame.service;

import com.springboot.matchgame.entity.MatchLevel;
import com.springboot.matchgame.entity.MatchWordPair;
import com.springboot.matchgame.repositories.MatchLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MatchDataInitializationService implements CommandLineRunner {

    @Autowired
    private MatchLevelRepository levelRepository;

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

        // Level 1 - Beginner
        List<MatchWordPair> level1Pairs = Arrays.asList(
                new MatchWordPair("Apple", "a round fruit with firm, white flesh and a green, red, or yellow skin"),
                new MatchWordPair("Dog", "A domestic animal"),
                new MatchWordPair("Sun", "The star at the center of our solar system"),
                new MatchWordPair("Book", "A collection of pages"),
                new MatchWordPair("Computer", "An electronic device for processing data")
        );
        MatchLevel level1 = new MatchLevel(
                "Level1", "Beginner", "Easy", level1Pairs
        );

        // Level 2 - Intermediate
        List<MatchWordPair> level2Pairs = Arrays.asList(
                new MatchWordPair("Flower", "A plant's reproductive organ"),
                new MatchWordPair("Tiger", "A big cat"),
                new MatchWordPair("River", "A natural water flow"),
                new MatchWordPair("Mountain", "A high landform"),
                new MatchWordPair("Car", "A road vehicle")
        );
        MatchLevel level2 = new MatchLevel(
                "Level2", "Intermediate", "Medium", level2Pairs
        );

        // Level 3 - Advanced
        List<MatchWordPair> level3Pairs = Arrays.asList(
                new MatchWordPair("Banana", "A yellow fruit"),
                new MatchWordPair("Cat", "A domestic feline"),
                new MatchWordPair("Moon", "Earth's natural satellite"),
                new MatchWordPair("Notebook", "A collection of blank pages"),
                new MatchWordPair("Phone", "A device for calling")
        );
        MatchLevel level3 = new MatchLevel(
                "Level3", "Advanced", "Hard", level3Pairs
        );

        // Level 4 - Expert
        List<MatchWordPair> level4Pairs = Arrays.asList(
                new MatchWordPair("Rose", "A type of flower"),
                new MatchWordPair("Lion", "The king of the jungle"),
                new MatchWordPair("Lake", "A body of water surrounded by land"),
                new MatchWordPair("Hill", "A small elevation of land"),
                new MatchWordPair("Bus", "A large passenger vehicle")
        );
        MatchLevel level4 = new MatchLevel(
                "Level4", "Expert", "Expert", level4Pairs
        );

        // Save levels
        levelRepository.saveAll(Arrays.asList(level1, level2, level3, level4));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}