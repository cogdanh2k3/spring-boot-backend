package com.springboot.matchgame.service;

import com.springboot.matchgame.entity.MatchGameLevel;
import com.springboot.matchgame.entity.MatchPair;
import com.springboot.matchgame.repositories.MatchGameLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MatchGameDataInitializationService implements CommandLineRunner {

    @Autowired
    private MatchGameLevelRepository levelRepository;

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
        List<MatchPair> easyPairs = Arrays.asList(
                new MatchPair("A farm animal that says moo", "COW"),
                new MatchPair("A purple vegetable", "EGGPLANT"),
                new MatchPair("A hot drink made from beans", "COFFEE"),
                new MatchPair("A fruit that is yellow and curved", "BANANA"),
                new MatchPair("A vehicle with two wheels", "BICYCLE"),
                new MatchPair("A pet that says meow", "CAT"),
                new MatchPair("The opposite of cold", "HOT"),
                new MatchPair("The color of the sky on a clear day", "BLUE"),
                new MatchPair("A round red fruit often used in salads", "TOMATO"),
                new MatchPair("The day after today", "TOMORROW")
        );
        MatchGameLevel easyLevel = new MatchGameLevel(
                "Easy", "Easy Level", "Easy", easyPairs
        );

        // Normal Level
        List<MatchPair> normalPairs = Arrays.asList(
                new MatchPair("A device for computing", "COMPUTER"),
                new MatchPair("A fruit that is red or green", "APPLE"),
                new MatchPair("A planet known as the Red Planet", "MARS"),
                new MatchPair("The opposite of up", "DOWN"),
                new MatchPair("A large animal with a trunk", "ELEPHANT"),
                new MatchPair("A tool used for cutting paper", "SCISSORS"),
                new MatchPair("The season after winter", "SPRING"),
                new MatchPair("The capital city of France", "PARIS"),
                new MatchPair("A flying mammal", "BAT"),
                new MatchPair("An instrument with six strings", "GUITAR")

        );
        MatchGameLevel normalLevel = new MatchGameLevel(
                "Normal", "Normal Level", "Normal", normalPairs
        );

        // Hard Level
        List<MatchPair> hardPairs = Arrays.asList(
                new MatchPair("The study of celestial objects", "ASTRONOMY"),
                new MatchPair("A programming language", "JAVA"),
                new MatchPair("The process of cell division", "MITOSIS"),
                new MatchPair("The largest ocean on Earth", "PACIFIC"),
                new MatchPair("The smallest prime number", "TWO"),
                new MatchPair("The capital city of Japan", "TOKYO"),
                new MatchPair("A branch of mathematics dealing with shapes", "GEOMETRY"),
                new MatchPair("The chemical symbol for gold", "AU"),
                new MatchPair("The author of 'Hamlet'", "SHAKESPEARE"),
                new MatchPair("The first element in the periodic table", "HYDROGEN")
        );
        MatchGameLevel hardLevel = new MatchGameLevel(
                "Hard", "Hard Level", "Hard", hardPairs
        );

        // Save levels
        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}