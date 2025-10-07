package com.springboot.soundgame.service;

import com.springboot.soundgame.entity.SoundClip;
import com.springboot.soundgame.entity.SoundLevel;
import com.springboot.soundgame.repository.SoundLevelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SoundDataInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SoundDataInitializationService.class);

    @Autowired
    private SoundLevelRepository levelRepository;

    @Autowired
    private FreesoundClient freesoundClient;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLevels();
    }

    private void initializeDefaultLevels() {
        if (levelRepository.count() > 0) {
            log.info("Sound levels already exist, skipping initialization");
            return;
        }

        log.info("Initializing default sound levels from Freesound API...");

        try {
            // EASY LEVEL - Common everyday sounds
            List<SoundClip> easyClips = new ArrayList<>();

            log.info("Fetching easy level sounds...");
            easyClips.addAll(fetchSoundsForLevel("dog bark", "dog", "easy", 1));
            easyClips.addAll(fetchSoundsForLevel("cat meow", "cat", "easy", 2));
            easyClips.addAll(fetchSoundsForLevel("car horn", "car", "easy", 3));
            easyClips.addAll(fetchSoundsForLevel("doorbell", "doorbell", "easy", 4));
            easyClips.addAll(fetchSoundsForLevel("baby crying", "baby", "easy", 5));
            easyClips.addAll(fetchSoundsForLevel("phone ringing", "phone", "easy", 6));
            easyClips.addAll(fetchSoundsForLevel("clock ticking", "clock", "easy", 7));
            easyClips.addAll(fetchSoundsForLevel("water dripping", "water", "easy", 8));
            easyClips.addAll(fetchSoundsForLevel("bird chirping", "bird", "easy", 9));
            easyClips.addAll(fetchSoundsForLevel("thunder storm", "thunder", "easy", 10));

            if (!easyClips.isEmpty()) {
                SoundLevel easyLevel = new SoundLevel("LevelEasy", "Easy Level", "Easy", easyClips);
                levelRepository.save(easyLevel);
                log.info("Easy level created with {} clips", easyClips.size());
            }

            // NORMAL LEVEL - Less common but recognizable sounds
            List<SoundClip> normalClips = new ArrayList<>();

            log.info("Fetching normal level sounds...");
            normalClips.addAll(fetchSoundsForLevel("elevator", "elevator", "normal", 1));
            normalClips.addAll(fetchSoundsForLevel("typing keyboard", "keyboard", "normal", 2));
            normalClips.addAll(fetchSoundsForLevel("rain falling", "rain", "normal", 3));
            normalClips.addAll(fetchSoundsForLevel("glass breaking", "glass", "normal", 4));
            normalClips.addAll(fetchSoundsForLevel("zipper", "zipper", "normal", 5));
            normalClips.addAll(fetchSoundsForLevel("camera shutter", "camera", "normal", 6));
            normalClips.addAll(fetchSoundsForLevel("footsteps", "footsteps", "normal", 7));
            normalClips.addAll(fetchSoundsForLevel("microwave beep", "microwave", "normal", 8));
            normalClips.addAll(fetchSoundsForLevel("blender", "blender", "normal", 9));
            normalClips.addAll(fetchSoundsForLevel("vacuum cleaner", "vacuum", "normal", 10));

            if (!normalClips.isEmpty()) {
                SoundLevel normalLevel = new SoundLevel("LevelNormal", "Normal Level", "Normal", normalClips);
                levelRepository.save(normalLevel);
                log.info("Normal level created with {} clips", normalClips.size());
            }

            // HARD LEVEL - Musical instruments and complex sounds
            List<SoundClip> hardClips = new ArrayList<>();

            log.info("Fetching hard level sounds...");
            hardClips.addAll(fetchSoundsForLevel("piano note", "piano", "hard", 1));
            hardClips.addAll(fetchSoundsForLevel("violin", "violin", "hard", 2));
            hardClips.addAll(fetchSoundsForLevel("chainsaw", "chainsaw", "hard", 3));
            hardClips.addAll(fetchSoundsForLevel("helicopter", "helicopter", "hard", 4));
            hardClips.addAll(fetchSoundsForLevel("accordion", "accordion", "hard", 5));
            hardClips.addAll(fetchSoundsForLevel("saxophone", "saxophone", "hard", 6));
            hardClips.addAll(fetchSoundsForLevel("cricket chirping", "cricket", "hard", 7));
            hardClips.addAll(fetchSoundsForLevel("owl hooting", "owl", "hard", 8));
            hardClips.addAll(fetchSoundsForLevel("cash register", "cash register", "hard", 9));
            hardClips.addAll(fetchSoundsForLevel("morse code", "morse code", "hard", 10));

            if (!hardClips.isEmpty()) {
                SoundLevel hardLevel = new SoundLevel("LevelHard", "Hard Level", "Hard", hardClips);
                levelRepository.save(hardLevel);
                log.info("Hard level created with {} clips", hardClips.size());
            }

            log.info("Sound levels initialization completed successfully!");

        } catch (Exception e) {
            log.error("Failed to fetch sounds from Freesound API, using fallback data", e);
            initializeFallbackLevels();
        }
    }

    /**
     * Fetch sounds from Freesound API for a specific query
     */
    private List<SoundClip> fetchSoundsForLevel(String query, String answer, String difficulty, int index) {
        List<SoundClip> clips = new ArrayList<>();
        try {
            log.debug("Searching Freesound for: {}", query);
            List<FreesoundClient.FreesoundResult> results = freesoundClient.search(query, 1);

            if (!results.isEmpty()) {
                FreesoundClient.FreesoundResult result = results.get(0);
                String clipId = difficulty + index;
                SoundClip clip = new SoundClip(
                        clipId,
                        result.name,
                        result.previewUrl,
                        answer
                );
                clips.add(clip);
                log.debug("Added clip: {} - {}", clipId, result.name);
            } else {
                log.warn("No results found for query: {}", query);
            }
        } catch (Exception e) {
            log.error("Error fetching sound for query '{}': {}", query, e.getMessage());
        }
        return clips;
    }

    /**
     * Fallback data in case Freesound API is unavailable
     */
    private void initializeFallbackLevels() {
        log.info("Initializing fallback sound levels...");

        // Easy Level - Fallback data
        List<SoundClip> easyClips = Arrays.asList(
                new SoundClip("easy5", "Baby Crying", "https://freesound.org/data/previews/444/444444-hq.mp3", "baby")
        );
        SoundLevel easyLevel = new SoundLevel("LevelEasy", "Easy Level", "Easy", easyClips);

        // Normal Level - Fallback data
        List<SoundClip> normalClips = Arrays.asList(
                new SoundClip("normal5", "Zipper", "https://freesound.org/data/previews/567/567890-hq.mp3", "zipper")
        );
        SoundLevel normalLevel = new SoundLevel("LevelNormal", "Normal Level", "Normal", normalClips);

        // Hard Level - Fallback data
        List<SoundClip> hardClips = Arrays.asList(
                new SoundClip("hard5", "Accordion", "https://freesound.org/data/previews/123/123456-hq.mp3", "accordion")
        );
        SoundLevel hardLevel = new SoundLevel("LevelHard", "Hard Level", "Hard", hardClips);

        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));
        log.info("Fallback sound levels initialized successfully");
    }
}