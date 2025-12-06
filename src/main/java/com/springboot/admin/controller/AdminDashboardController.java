package com.springboot.admin.controller;

import com.springboot.auth.service.AuthService;
import com.springboot.wordsearch.service.WordSearchService;
import com.springboot.batchu.service.BatChuService;
import com.springboot.matchgame.service.MatchService;
import com.springboot.quizgame.service.QuizService;
import com.springboot.scene.service.SceneService;
import com.springboot.soundgame.service.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminDashboardController {

    @Autowired
    private AuthService authService;

    @Autowired
    private WordSearchService wordSearchService;

    @Autowired
    private BatChuService batChuService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SoundService soundService;

    // Middleware to check admin access
    private ResponseEntity<?> checkAdminAccess(String username) {
        if (!authService.isUserAdmin(username)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Access denied. Admin privileges required.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        return null;
    }

    // Get dashboard statistics
    @GetMapping("/dashboard/{username}")
    public ResponseEntity<?> getDashboardStats(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        Map<String, Object> stats = new HashMap<>();

        // Word Search stats
        stats.put("wordSearchTopics", wordSearchService.getAllTopics().size());

        // Bat Chu stats
        stats.put("batChuLevels", batChuService.getAllLevels().size());

        // Match Game stats
        stats.put("matchLevels", matchService.getAllLevels().size());

        // Quiz Game stats
        stats.put("quizLevels", quizService.getAllLevels().size());

        // Scene Game stats
        stats.put("sceneLevels", sceneService.getAllLevels().size());

        // Sound Game stats
        stats.put("soundLevels", soundService.getAllLevels().size());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", stats);

        return ResponseEntity.ok(response);
    }

    // Word Search Management
    @GetMapping("/wordsearch/{username}")
    public ResponseEntity<?> getWordSearchTopics(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "topics", wordSearchService.getAllTopics()
        ));
    }

    // Bat Chu Management
    @GetMapping("/batchu/{username}")
    public ResponseEntity<?> getBatChuLevels(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "levels", batChuService.getAllLevels()
        ));
    }

    // Match Game Management
    @GetMapping("/matchgame/{username}")
    public ResponseEntity<?> getMatchLevels(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "levels", matchService.getAllLevels()
        ));
    }

    // Quiz Game Management
    @GetMapping("/quiz/{username}")
    public ResponseEntity<?> getQuizLevels(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "levels", quizService.getAllLevels()
        ));
    }

    // Scene Game Management
    @GetMapping("/scene/{username}")
    public ResponseEntity<?> getSceneLevels(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "levels", sceneService.getAllLevels()
        ));
    }

    // Sound Game Management
    @GetMapping("/sound/{username}")
    public ResponseEntity<?> getSoundLevels(@PathVariable String username) {
        ResponseEntity<?> accessCheck = checkAdminAccess(username);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(Map.of(
                "success", true,
                "levels", soundService.getAllLevels()
        ));
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy", "service", "admin-dashboard"));
    }
}