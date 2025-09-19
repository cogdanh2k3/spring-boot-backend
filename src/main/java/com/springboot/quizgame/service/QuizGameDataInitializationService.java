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

        // ================= Easy Level =================
        List<QuizQuestion> easyQuestions = Arrays.asList(
                new QuizQuestion("Leave the driving to us! was a phrase advertising which company?", "Greyhound", "world", Arrays.asList("Pan Am Airlines", "Amtrak", "Greyhound", "Hertz")),
                new QuizQuestion("The name of this city means the capital in the native language. It is the capital of South Korea.", "Seoul", "world", Arrays.asList("Seoul", "Taegu", "Taejeon", "Pusan")),
                new QuizQuestion("Almost half of the 56 delegates who signed the US Declaration of Independence were representatives of what profession?", "Lawyers and jurists", "world", Arrays.asList("Teachers and scholars", "Farmers and plantation owners", "Merchants and traders", "Lawyers and jurists")),
                new QuizQuestion("What day was proclaimed an European Day of Languages by the Council of Europe?", "26 September", "world", Arrays.asList("26 September", "26 May", "26 December", "26 January")),
                new QuizQuestion("This mountain contains the highest peak in Antarctica, and is located about 750 miles (1,200 km) from the South Pole.", "Vinson Massif", "world", Arrays.asList("Mount Jackson", "Vinson Massif", "Mount Terror", "Craddock Massif")),
                new QuizQuestion("Which Italian city is famous for its canals?", "Venice", "world", Arrays.asList("Rome", "Venice", "Florence", "Milan")),
                new QuizQuestion("Which gas do plants absorb from the atmosphere?", "Carbon Dioxide", "world", Arrays.asList("Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen")),
                new QuizQuestion("Which planet is known as the Red Planet?", "Mars", "world", Arrays.asList("Mars", "Venus", "Jupiter", "Saturn")),
                new QuizQuestion("What is the capital of France?", "Paris", "world", Arrays.asList("Berlin", "Paris", "Madrid", "Rome")),
                new QuizQuestion("What is H2O more commonly known as?", "Water", "world", Arrays.asList("Water", "Salt", "Oxygen", "Hydrogen")),
                new QuizQuestion("How many continents are there?", "7", "world", Arrays.asList("5", "6", "7", "8")),
                new QuizQuestion("Which animal is known as the King of the Jungle?", "Lion", "world", Arrays.asList("Tiger", "Lion", "Elephant", "Leopard")),
                new QuizQuestion("What is the capital of Japan?", "Tokyo", "world", Arrays.asList("Kyoto", "Seoul", "Tokyo", "Beijing")),
                new QuizQuestion("What is the largest mammal?", "Blue Whale", "world", Arrays.asList("Elephant", "Blue Whale", "Shark", "Giraffe")),
                new QuizQuestion("Which fruit keeps the doctor away if eaten daily?", "Apple", "world", Arrays.asList("Banana", "Orange", "Apple", "Grapes")),
                new QuizQuestion("What color is a ripe banana?", "Yellow", "world", Arrays.asList("Green", "Red", "Yellow", "Orange")),
                new QuizQuestion("What is the tallest animal?", "Giraffe", "world", Arrays.asList("Elephant", "Horse", "Giraffe", "Camel")),
                new QuizQuestion("Which bird is known for mimicking sounds?", "Parrot", "world", Arrays.asList("Parrot", "Crow", "Eagle", "Owl")),
                new QuizQuestion("Which shape has 3 sides?", "Triangle", "world", Arrays.asList("Square", "Triangle", "Circle", "Pentagon")),
                new QuizQuestion("Which animal barks?", "Dog", "world", Arrays.asList("Cat", "Cow", "Dog", "Duck"))
        );

        QuizGameLevel easyLevel = new QuizGameLevel("LevelEasy", "Easy Level", "Easy", easyQuestions);


// ================= Normal Level =================
        List<QuizQuestion> normalQuestions = Arrays.asList(
                new QuizQuestion("Which planet has the most moons?", "Saturn", "world", Arrays.asList("Jupiter", "Mars", "Saturn", "Neptune")),
                new QuizQuestion("What is the chemical symbol for gold?", "Au", "world", Arrays.asList("Ag", "Au", "Pt", "Pb")),
                new QuizQuestion("Who painted the Mona Lisa?", "Leonardo da Vinci", "world", Arrays.asList("Michelangelo", "Raphael", "Leonardo da Vinci", "Donatello")),
                new QuizQuestion("Which desert is the largest in the world?", "Sahara", "world", Arrays.asList("Sahara", "Gobi", "Kalahari", "Atacama")),
                new QuizQuestion("Which element has atomic number 1?", "Hydrogen", "world", Arrays.asList("Oxygen", "Hydrogen", "Carbon", "Nitrogen")),
                new QuizQuestion("In which year did World War II end?", "1945", "world", Arrays.asList("1940", "1942", "1945", "1950")),
                new QuizQuestion("What is the longest river in the world?", "Nile", "world", Arrays.asList("Amazon", "Nile", "Yangtze", "Mississippi")),
                new QuizQuestion("Which blood type is known as the universal donor?", "O Negative", "world", Arrays.asList("A", "B", "AB", "O Negative")),
                new QuizQuestion("Which language has the most native speakers?", "Mandarin Chinese", "world", Arrays.asList("English", "Mandarin Chinese", "Spanish", "Hindi")),
                new QuizQuestion("What is the capital of Canada?", "Ottawa", "world", Arrays.asList("Toronto", "Vancouver", "Ottawa", "Montreal")),
                // thêm cho đủ 20
                new QuizQuestion("What is the square root of 144?", "12", "math", Arrays.asList("10", "11", "12", "13")),
                new QuizQuestion("Which gas makes up most of the Earth's atmosphere?", "Nitrogen", "world", Arrays.asList("Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen")),
                new QuizQuestion("Which organ purifies blood in the human body?", "Kidney", "world", Arrays.asList("Heart", "Liver", "Kidney", "Lung")),
                new QuizQuestion("Which metal is liquid at room temperature?", "Mercury", "world", Arrays.asList("Mercury", "Iron", "Gold", "Silver")),
                new QuizQuestion("Who discovered gravity?", "Isaac Newton", "world", Arrays.asList("Albert Einstein", "Isaac Newton", "Galileo", "Kepler")),
                new QuizQuestion("Which country gifted the Statue of Liberty to the USA?", "France", "world", Arrays.asList("France", "UK", "Spain", "Germany")),
                new QuizQuestion("What is the national sport of Japan?", "Sumo Wrestling", "world", Arrays.asList("Karate", "Sumo Wrestling", "Judo", "Baseball")),
                new QuizQuestion("Which vitamin is produced when skin is exposed to sunlight?", "Vitamin D", "world", Arrays.asList("Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D")),
                new QuizQuestion("Which instrument measures atmospheric pressure?", "Barometer", "world", Arrays.asList("Thermometer", "Barometer", "Hygrometer", "Anemometer")),
                new QuizQuestion("What is the currency of Switzerland?", "Swiss Franc", "world", Arrays.asList("Euro", "Swiss Franc", "Dollar", "Pound"))
        );

        QuizGameLevel normalLevel = new QuizGameLevel("LevelNormal", "Normal Level", "Normal", normalQuestions);


// ================= Hard Level =================
        List<QuizQuestion> hardQuestions = Arrays.asList(
                new QuizQuestion("What is the heaviest naturally occurring element?", "Uranium", "world", Arrays.asList("Lead", "Uranium", "Plutonium", "Mercury")),
                new QuizQuestion("Who developed the theory of general relativity?", "Albert Einstein", "world", Arrays.asList("Isaac Newton", "Albert Einstein", "Nikola Tesla", "Max Planck")),
                new QuizQuestion("Which is the only planet that rotates on its side?", "Uranus", "world", Arrays.asList("Uranus", "Venus", "Mars", "Neptune")),
                new QuizQuestion("What is the rarest blood type?", "AB Negative", "world", Arrays.asList("O Positive", "A Positive", "AB Negative", "B Negative")),
                new QuizQuestion("What is the largest internal organ in the human body?", "Liver", "world", Arrays.asList("Heart", "Liver", "Kidney", "Lung")),
                new QuizQuestion("Which physicist is known as the father of the atomic bomb?", "Robert Oppenheimer", "world", Arrays.asList("Robert Oppenheimer", "Enrico Fermi", "Niels Bohr", "Werner Heisenberg")),
                new QuizQuestion("What is the capital of Iceland?", "Reykjavik", "world", Arrays.asList("Oslo", "Reykjavik", "Helsinki", "Copenhagen")),
                new QuizQuestion("Which scientist introduced the concept of natural selection?", "Charles Darwin", "world", Arrays.asList("Charles Darwin", "Gregor Mendel", "Louis Pasteur", "Jean Lamarck")),
                new QuizQuestion("Which chemical has the formula C6H12O6?", "Glucose", "world", Arrays.asList("Glucose", "Fructose", "Sucrose", "Lactose")),
                new QuizQuestion("Which year did the French Revolution begin?", "1789", "world", Arrays.asList("1776", "1789", "1812", "1848")),
                // thêm cho đủ 20
                new QuizQuestion("What is the study of fungi called?", "Mycology", "world", Arrays.asList("Botany", "Mycology", "Zoology", "Ecology")),
                new QuizQuestion("What is the capital of Kazakhstan?", "Astana", "world", Arrays.asList("Almaty", "Astana", "Tashkent", "Bishkek")),
                new QuizQuestion("Which gas is known as laughing gas?", "Nitrous Oxide", "world", Arrays.asList("Carbon Monoxide", "Nitrous Oxide", "Methane", "Ammonia")),
                new QuizQuestion("What is the hardest natural substance on Earth?", "Diamond", "world", Arrays.asList("Iron", "Diamond", "Quartz", "Granite")),
                new QuizQuestion("Who is the author of 'War and Peace'?", "Leo Tolstoy", "world", Arrays.asList("Leo Tolstoy", "Fyodor Dostoevsky", "Anton Chekhov", "Ivan Turgenev")),
                new QuizQuestion("Which particle has a negative charge?", "Electron", "world", Arrays.asList("Proton", "Neutron", "Electron", "Photon")),
                new QuizQuestion("Which is the deepest ocean trench?", "Mariana Trench", "world", Arrays.asList("Tonga Trench", "Mariana Trench", "Java Trench", "Puerto Rico Trench")),
                new QuizQuestion("What is the SI unit of force?", "Newton", "world", Arrays.asList("Joule", "Pascal", "Newton", "Watt")),
                new QuizQuestion("Who proposed the three laws of motion?", "Isaac Newton", "world", Arrays.asList("Galileo", "Einstein", "Isaac Newton", "Kepler")),
                new QuizQuestion("What is the currency of Denmark?", "Krone", "world", Arrays.asList("Euro", "Krone", "Pound", "Dollar"))
        );

        QuizGameLevel hardLevel = new QuizGameLevel("LevelHard", "Hard Level", "Hard", hardQuestions);


        // Save levels
        levelRepository.saveAll(Arrays.asList(easyLevel, normalLevel, hardLevel));

        System.out.println("Successfully initialized " + levelRepository.count() + " levels");
    }
}