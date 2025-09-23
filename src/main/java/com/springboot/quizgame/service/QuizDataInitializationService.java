package com.springboot.quizgame.service;

import com.springboot.quizgame.entity.QuizLevel;
import com.springboot.quizgame.entity.QuizQuestion;
import com.springboot.quizgame.repositories.QuizLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class QuizDataInitializationService implements CommandLineRunner {

    @Autowired
    private QuizLevelRepository levelRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultLevels();
    }

    private void initializeDefaultLevels() {
        if (levelRepository.count() > 0) {
            System.out.println("Levels have been initialized, skipping");
            return;
        }

        System.out.println("Initializing default levels...");

        // Easy Level
        List<QuizQuestion> easyQuestions = Arrays.asList(
                new QuizQuestion(
                        "Which country is known for the Eiffel Tower?",
                        "France",
                        "geography",
                        Arrays.asList("Italy", "France", "Spain", "Germany")
                ),
                new QuizQuestion(
                        "What is 2 + 2?",
                        "4",
                        "math",
                        Arrays.asList("3", "4", "5", "6")
                ),
                new QuizQuestion(
                        "Which planet is known as the Red Planet?",
                        "Mars",
                        "science",
                        Arrays.asList("Jupiter", "Venus", "Mars", "Mercury")
                ),
                new QuizQuestion(
                        "What is the capital of Japan?",
                        "Tokyo",
                        "geography",
                        Arrays.asList("Beijing", "Seoul", "Tokyo", "Bangkok")
                ),
                new QuizQuestion(
                        "Which animal is known as man's best friend?",
                        "Dog",
                        "general",
                        Arrays.asList("Cat", "Dog", "Horse", "Rabbit")
                ),
                new QuizQuestion(
                        "What color is the sky on a clear day?",
                        "Blue",
                        "general",
                        Arrays.asList("Red", "Green", "Blue", "Yellow")
                ),
                new QuizQuestion(
                        "Which fruit is known for keeping the doctor away?",
                        "Apple",
                        "general",
                        Arrays.asList("Banana", "Orange", "Apple", "Grape")
                ),
                new QuizQuestion(
                        "How many days are in a week?",
                        "7",
                        "general",
                        Arrays.asList("5", "6", "7", "8")
                ),
                new QuizQuestion(
                        "What is the largest ocean on Earth?",
                        "Pacific Ocean",
                        "geography",
                        Arrays.asList("Atlantic Ocean", "Indian Ocean", "Pacific Ocean", "Arctic Ocean")
                ),
                new QuizQuestion(
                        "Which bird is a symbol of peace?",
                        "Dove",
                        "general",
                        Arrays.asList("Eagle", "Dove", "Owl", "Parrot")
                )
        );
        QuizLevel easyLevel = new QuizLevel(
                "LevelEasy", "Easy Level", "Easy", easyQuestions
        );

        // Normal Level
        List<QuizQuestion> normalQuestions = Arrays.asList(
                new QuizQuestion(
                        "Who wrote 'Romeo and Juliet'?",
                        "William Shakespeare",
                        "literature",
                        Arrays.asList("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain")
                ),
                new QuizQuestion(
                        "What is the capital of Brazil?",
                        "Brasília",
                        "geography",
                        Arrays.asList("Rio de Janeiro", "São Paulo", "Brasília", "Salvador")
                ),
                new QuizQuestion(
                        "What is the chemical symbol for water?",
                        "H2O",
                        "science",
                        Arrays.asList("CO2", "H2O", "O2", "N2")
                ),
                new QuizQuestion(
                        "Which year did World War II end?",
                        "1945",
                        "history",
                        Arrays.asList("1940", "1943", "1945", "1947")
                ),
                new QuizQuestion(
                        "What is the largest planet in our solar system?",
                        "Jupiter",
                        "science",
                        Arrays.asList("Mars", "Jupiter", "Saturn", "Earth")
                ),
                new QuizQuestion(
                        "Who painted the Mona Lisa?",
                        "Leonardo da Vinci",
                        "art",
                        Arrays.asList("Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Claude Monet")
                ),
                new QuizQuestion(
                        "What is the currency of the United Kingdom?",
                        "Pound",
                        "general",
                        Arrays.asList("Euro", "Dollar", "Pound", "Yen")
                ),
                new QuizQuestion(
                        "Which country hosted the 2016 Summer Olympics?",
                        "Brazil",
                        "history",
                        Arrays.asList("China", "Brazil", "Japan", "Russia")
                ),
                new QuizQuestion(
                        "What is the square root of 64?",
                        "8",
                        "math",
                        Arrays.asList("6", "7", "8", "9")
                ),
                new QuizQuestion(
                        "Which gas is most abundant in Earth's atmosphere?",
                        "Nitrogen",
                        "science",
                        Arrays.asList("Oxygen", "Carbon Dioxide", "Nitrogen", "Argon")
                )
        );
        QuizLevel normalLevel = new QuizLevel(
                "LevelNormal", "Normal Level", "Normal", normalQuestions
        );

        // Hard Level
        List<QuizQuestion> hardQuestions = Arrays.asList(
                new QuizQuestion(
                        "What year was the United Nations founded?",
                        "1945",
                        "history",
                        Arrays.asList("1942", "1943", "1944", "1945")
                ),
                new QuizQuestion(
                        "Which element has the atomic number 79?",
                        "Gold",
                        "science",
                        Arrays.asList("Silver", "Gold", "Copper", "Iron")
                ),
                new QuizQuestion(
                        "Who discovered the theory of relativity?",
                        "Albert Einstein",
                        "science",
                        Arrays.asList("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Nikola Tesla")
                ),
                new QuizQuestion(
                        "What is the capital of Iceland?",
                        "Reykjavik",
                        "geography",
                        Arrays.asList("Oslo", "Helsinki", "Reykjavik", "Stockholm")
                ),
                new QuizQuestion(
                        "Which philosopher wrote 'The Republic'?",
                        "Plato",
                        "philosophy",
                        Arrays.asList("Aristotle", "Socrates", "Plato", "Immanuel Kant")
                ),
                new QuizQuestion(
                        "What is the boiling point of water in Celsius at sea level?",
                        "100",
                        "science",
                        Arrays.asList("80", "90", "100", "110")
                ),
                new QuizQuestion(
                        "Which country is the largest by land area?",
                        "Russia",
                        "geography",
                        Arrays.asList("Canada", "China", "Russia", "United States")
                ),
                new QuizQuestion(
                        "Who composed the symphony 'Eroica'?",
                        "Ludwig van Beethoven",
                        "music",
                        Arrays.asList("Wolfgang Amadeus Mozart", "Ludwig van Beethoven", "Johann Sebastian Bach", "Franz Schubert")
                ),
                new QuizQuestion(
                        "What is the chemical formula for table salt?",
                        "NaCl",
                        "science",
                        Arrays.asList("KCl", "NaCl", "CaCl2", "MgCl2")
                ),
                new QuizQuestion(
                        "Which war was fought between the Allies and the Axis powers?",
                        "World War II",
                        "history",
                        Arrays.asList("World War I", "World War II", "Cold War", "Korean War")
                )
        );
        QuizLevel hardLevel = new QuizLevel(
                "LevelHard", "Hard Level", "Hard", hardQuestions
        );

        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}