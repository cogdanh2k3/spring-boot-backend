package com.springboot.batchu.service;

import com.springboot.batchu.entity.batChuLevel;
import com.springboot.batchu.entity.batChuQuestion;
import com.springboot.batchu.repositories.BatChuLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BatChuDataInitializationService implements CommandLineRunner {

    @Autowired
    private BatChuLevelRepository levelRepository;

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
        List<batChuQuestion> easyQuestions = Arrays.asList(
                new batChuQuestion("EGGPLANT", "EGGPLANT", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Feggplant.jpg?alt=media&token=c8ea59b6-b082-43c0-bb27-023bec97e963", ""),
                new batChuQuestion("COWBOY", "COWBOY", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fcowboy.jpg?alt=media&token=ecf10d57-a3c1-4873-8082-c62bfd76429e", ""),
                new batChuQuestion("LIPSTICK", "LIPSTICK", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Flipstick.jpg?alt=media&token=c8b6e963-c47e-4ed3-badc-e37568ae4496", ""),
                new batChuQuestion("GOLDFISH", "GOLDFISH", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fgoldfish.jpg?alt=media&token=67970e24-3e11-4fc5-a423-1b076fc45515", ""),
                new batChuQuestion("FIREMAN", "FIREMAN", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Ffireman.jpg?alt=media&token=ae9734d1-8cfb-478b-866b-d9243c737af5", "")

        );
        batChuLevel easyLevel = new batChuLevel(
                "LevelEasy", "Easy Level", "Easy", easyQuestions
        );

        // Easy Level
        List<batChuQuestion> normalQuestions = Arrays.asList(
                new batChuQuestion("SNAPSHOT", "SNAPSHOT", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fsnapshot.jpg?alt=media&token=ff09654e-4a17-434c-97a9-459ca165db76", ""),
                new batChuQuestion("STARFISH", "STARFISH", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fstarfish.jpg?alt=media&token=3663041c-87a5-41ca-9a8d-34231973317e", ""),
                new batChuQuestion("MEATBALL", "MEATBALL", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fmeatball.jpg?alt=media&token=b2c4d163-e355-4568-ac88-47ba0eeb887e", ""),
                new batChuQuestion("SUNFLOWER", "SUNFLOWER", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fsunflower.jpg?alt=media&token=f0a84dff-80c5-4950-9c64-ae4f0c62fd4b", ""),
                new batChuQuestion("SUNGLASSES", "SUNGLASSES", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fsunglasses.jpg?alt=media&token=5a7dead7-f486-4577-b25e-62993bbb81fc", "")

        );
        batChuLevel normalLevel = new batChuLevel(
                "LevelNormal", "Normal Level", "Normal", normalQuestions
        );

        // Easy Level
        List<batChuQuestion> hardQuestions = Arrays.asList(
                new batChuQuestion("ROOMMATE", "ROOMMATE", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fheadphone.jpg?alt=media&token=416b1021-e5c8-4d20-80fe-be0fa6e549c6", ""),
                new batChuQuestion("OUTDOOR", "OUTDOOR", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fmakeup.jpg?alt=media&token=00c7a819-90bc-433a-8ebc-6017be3b62de", ""),
                new batChuQuestion("MEATBALL", "MEATBALL", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fmeatball.jpg?alt=media&token=b2c4d163-e355-4568-ac88-47ba0eeb887e", ""),
                new batChuQuestion("MAKEUP", "MAKEUP", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Foutdoor.jpg?alt=media&token=a57500f8-18dc-4e62-81d9-d67e41715c96", ""),
                new batChuQuestion("HEADPHONE", "HEADPHONE", "https://firebasestorage.googleapis.com/v0/b/eduquizz-7115e.firebasestorage.app/o/EN%2Fheadphone.jpg?alt=media&token=416b1021-e5c8-4d20-80fe-be0fa6e549c6", "")

        );
        batChuLevel hardLevel = new batChuLevel(
                "LevelHard", "Hard Level", "Hard", hardQuestions
        );

        // Save levels
        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}