package com.springboot.game.controller;

import com.springboot.game.entity.GameSession;
import com.springboot.game.service.GameValidationService;
import com.springboot.game.service.GameValidationService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game Session Controller - API endpoints cho game validation
 * 
 * Endpoints:
 * - POST /api/game/session/start - Bắt đầu session mới
 * - POST /api/game/session/submit - Submit kết quả game
 */
@RestController
@RequestMapping("/api/game/session")
@CrossOrigin(origins = "*")
public class GameSessionController {

    private final GameValidationService validationService;

    @Autowired
    public GameSessionController(GameValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Start a new game session
     * 
     * Client gọi API này trước khi bắt đầu chơi
     * Server trả về sessionId và lưu câu hỏi để verify sau
     */
    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody StartSessionRequest request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            GameSession session = validationService.createSession(
                    userId,
                    request.getGameType(),
                    request.getLevelId(),
                    request.getQuestions());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sessionId", session.getSessionId());
            response.put("startTime", session.getStartTime().toString());

            System.out.println("🎮 [GAME] Session started: " + session.getSessionId() +
                    " for user " + userId + ", game: " + request.getGameType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Submit game results for validation
     * 
     * Client gửi:
     * - sessionId: ID của session đã tạo
     * - answers: Danh sách câu trả lời với timing
     * - clientScore: Điểm client tính được
     * - signature: HMAC signature để verify
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitScore(@RequestBody SubmitScoreRequest request) {
        try {
            System.out.println("🎯 [GAME] Score submission for session: " + request.getSessionId());

            ValidationResult result = validationService.validateSubmission(
                    request.getSessionId(),
                    request.getAnswers(),
                    request.getClientScore(),
                    request.getSignature());

            if (!result.success) {
                System.out.println("❌ [GAME] Validation FAILED: " + result.errorCode +
                        " - " + result.errorMessage);

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("errorCode", result.errorCode);
                errorResponse.put("errorMessage", result.errorMessage);

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            System.out.println("✅ [GAME] Validation SUCCESS: score=" + result.verifiedScore +
                    ", suspicious=" + result.flaggedSuspicious);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("verifiedScore", result.verifiedScore);
            response.put("flaggedSuspicious", result.flaggedSuspicious);

            if (result.flaggedSuspicious) {
                response.put("warning", "Your submission has been flagged for review");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("❌ [GAME] Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Generate signature for testing/debugging
     * (Should be removed in production)
     */
    @PostMapping("/generate-signature")
    public ResponseEntity<?> generateSignature(@RequestBody GenerateSignatureRequest request) {
        String signature = validationService.generateSignature(
                request.getSessionId(),
                request.getAnswers());
        return ResponseEntity.ok(Map.of("signature", signature));
    }

    /**
     * Get current user ID from Security Context
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() != null) {
            Object details = auth.getDetails();
            if (details instanceof com.springboot.auth.security.JwtAuthenticationFilter.JwtUserDetails) {
                return ((com.springboot.auth.security.JwtAuthenticationFilter.JwtUserDetails) details)
                        .getUserId();
            }
        }
        return null;
    }

    // ============ Request DTOs ============

    public static class StartSessionRequest {
        private String gameType;
        private String levelId;
        private List<QuestionData> questions;

        public String getGameType() {
            return gameType;
        }

        public void setGameType(String gameType) {
            this.gameType = gameType;
        }

        public String getLevelId() {
            return levelId;
        }

        public void setLevelId(String levelId) {
            this.levelId = levelId;
        }

        public List<QuestionData> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionData> questions) {
            this.questions = questions;
        }
    }

    public static class SubmitScoreRequest {
        private String sessionId;
        private List<AnswerData> answers;
        private int clientScore;
        private String signature;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public List<AnswerData> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswerData> answers) {
            this.answers = answers;
        }

        public int getClientScore() {
            return clientScore;
        }

        public void setClientScore(int clientScore) {
            this.clientScore = clientScore;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    public static class GenerateSignatureRequest {
        private String sessionId;
        private List<AnswerData> answers;

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public List<AnswerData> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswerData> answers) {
            this.answers = answers;
        }
    }
}
