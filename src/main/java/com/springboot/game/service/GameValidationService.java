package com.springboot.game.service;

import com.springboot.game.entity.GameSession;
import com.springboot.game.repository.GameSessionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Game Validation Service - Xác thực điểm số game
 * 
 * Chức năng:
 * 1. Tạo và quản lý game sessions
 * 2. Verify signature từ client
 * 3. Tính toán lại điểm từ answers
 * 4. Phát hiện bot/cheat bằng timing analysis
 */
@Service
@Transactional // Fix LOB stream error by keeping Hibernate session open
public class GameValidationService {

    private static final Logger log = LoggerFactory.getLogger(GameValidationService.class);

    private static final String HMAC_SECRET = "EduQuizz_Game_Session_Secret_2024";
    private static final long SESSION_EXPIRY_MINUTES = 30;
    private static final long MIN_ANSWER_TIME_MS = 500; // Minimum 500ms per question
    private static final int POINTS_PER_CORRECT = 10;

    private final GameSessionRepository sessionRepository;
    private final Gson gson = new Gson();

    @Autowired
    public GameValidationService(GameSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Tạo session mới cho game
     */
    public GameSession createSession(Long userId, String gameType, String levelId,
            List<QuestionData> questions) {
        String sessionId = generateSessionId();

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("🎮 [CREATE SESSION] Starting new session");
        log.info("   SessionId: {}", sessionId);
        log.info("   UserId: {}", userId);
        log.info("   GameType: {}", gameType);
        log.info("   LevelId: {}", levelId);
        log.info("   Questions count: {}", questions != null ? questions.size() : 0);

        GameSession session = new GameSession(sessionId, userId, gameType, levelId);
        String questionsJson = gson.toJson(questions);
        session.setQuestionsJson(questionsJson);

        log.info("   QuestionsJson length: {} chars", questionsJson.length());

        GameSession saved = sessionRepository.save(session);
        log.info("✅ [CREATE SESSION] Session saved successfully: {}", saved.getSessionId());
        log.info("═══════════════════════════════════════════════════════════════");

        return saved;
    }

    /**
     * Validate submission và tính toán điểm
     */
    public ValidationResult validateSubmission(String sessionId,
            List<AnswerData> answers,
            int clientScore,
            String signature) {

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("🎯 [VALIDATE] Starting validation for session: {}", sessionId);
        log.info("   Answers count: {}", answers != null ? answers.size() : 0);
        log.info("   Client score: {}", clientScore);
        log.info("   Signature: {}",
                signature != null ? signature.substring(0, Math.min(20, signature.length())) + "..." : "null");

        // 1. Check session exists
        log.info("📍 Step 1: Checking if session exists...");
        Optional<GameSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            log.error("❌ [VALIDATE] Session not found: {}", sessionId);
            return ValidationResult.error("INVALID_SESSION", "Session not found");
        }

        GameSession session = sessionOpt.get();
        log.info("   ✓ Session found: userId={}, gameType={}", session.getUserId(), session.getGameType());

        // 2. Check if already submitted
        log.info("📍 Step 2: Checking if already submitted...");
        if (session.isSubmitted()) {
            log.warn("⚠️ [VALIDATE] Session already submitted: {}", sessionId);
            return ValidationResult.error("SESSION_ALREADY_SUBMITTED",
                    "This session has already been submitted");
        }
        log.info("   ✓ Session not yet submitted");

        // 3. Check if expired
        log.info("📍 Step 3: Checking session expiry...");
        LocalDateTime expiryTime = session.getStartTime().plusMinutes(SESSION_EXPIRY_MINUTES);
        if (expiryTime.isBefore(LocalDateTime.now())) {
            log.warn("⚠️ [VALIDATE] Session expired. Started: {}, Expiry: {}",
                    session.getStartTime(), expiryTime);
            session.setExpired(true);
            sessionRepository.save(session);
            return ValidationResult.error("SESSION_EXPIRED", "Session has expired");
        }
        log.info("   ✓ Session still valid until: {}", expiryTime);

        // 4. Verify signature
        log.info("📍 Step 4: Verifying HMAC signature...");
        String expectedSignature = generateSignature(sessionId, answers);
        log.info("   Expected signature: {}",
                expectedSignature.substring(0, Math.min(20, expectedSignature.length())) + "...");
        log.info("   Received signature: {}",
                signature != null ? signature.substring(0, Math.min(20, signature.length())) + "..." : "null");

        if (!expectedSignature.equals(signature)) {
            log.error("❌ [VALIDATE] Signature mismatch! Possible tampering detected.");
            session.setSuspicious(true);
            session.setSuspiciousReason("Invalid signature - possible tampering");
            sessionRepository.save(session);
            return ValidationResult.error("INVALID_SIGNATURE",
                    "Signature verification failed");
        }
        log.info("   ✓ Signature verified successfully");

        // 5. Calculate server-side score
        log.info("📍 Step 5: Calculating server-side score...");
        String questionsJson = session.getQuestionsJson();
        log.info("   QuestionsJson length: {}", questionsJson != null ? questionsJson.length() : 0);

        List<QuestionData> questions = parseQuestions(questionsJson);
        log.info("   Parsed {} questions from JSON", questions.size());

        int verifiedScore = calculateScore(questions, answers);
        log.info("   ✓ Server calculated score: {}", verifiedScore);

        // 6. Check if client score matches
        log.info("📍 Step 6: Comparing scores...");
        log.info("   Client score: {}", clientScore);
        log.info("   Server score: {}", verifiedScore);

        if (verifiedScore != clientScore) {
            log.error("❌ [VALIDATE] Score mismatch! Client={}, Server={}", clientScore, verifiedScore);
            session.setSuspicious(true);
            session.setSuspiciousReason(
                    String.format("Score mismatch: client=%d, server=%d",
                            clientScore, verifiedScore));
            sessionRepository.save(session);
            return ValidationResult.error("SCORE_MISMATCH",
                    "Client score does not match calculated score");
        }
        log.info("   ✓ Scores match!");

        // 7. Check for suspicious timing (bot detection)
        log.info("📍 Step 7: Analyzing timing patterns...");
        TimingAnalysis timing = analyzeAnswerTiming(answers);
        if (timing.isSuspicious) {
            log.warn("⚠️ [VALIDATE] Suspicious timing detected: {}", timing.reason);
            session.setSuspicious(true);
            session.setSuspiciousReason("Suspicious timing: " + timing.reason);
        } else {
            log.info("   ✓ Timing analysis passed");
        }

        // 8. Save session with results
        log.info("📍 Step 8: Saving validated session...");
        session.setSubmitted(true);
        session.setEndTime(LocalDateTime.now());
        session.setVerifiedScore(verifiedScore);
        session.setClientScore(clientScore);
        session.setSignature(signature);
        session.setAnswersJson(gson.toJson(answers));
        sessionRepository.save(session);

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("✅ [VALIDATE] VALIDATION SUCCESS!");
        log.info("   Session: {}", sessionId);
        log.info("   Verified Score: {}", verifiedScore);
        log.info("   Flagged Suspicious: {}", timing.isSuspicious);
        log.info("═══════════════════════════════════════════════════════════════");

        return ValidationResult.success(verifiedScore, timing.isSuspicious);
    }

    /**
     * Generate session ID (UUID based)
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Generate HMAC signature for verification
     */
    public String generateSignature(String sessionId, List<AnswerData> answers) {
        try {
            StringBuilder dataBuilder = new StringBuilder(sessionId);
            for (AnswerData answer : answers) {
                dataBuilder.append("|")
                        .append(answer.questionId)
                        .append(":")
                        .append(answer.answer);
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(dataBuilder.toString().getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    /**
     * Parse questions JSON
     */
    private List<QuestionData> parseQuestions(String json) {
        if (json == null || json.isEmpty()) {
            log.warn("Questions JSON is null or empty");
            return new ArrayList<>();
        }
        try {
            return gson.fromJson(json, new TypeToken<List<QuestionData>>() {
            }.getType());
        } catch (Exception e) {
            log.error("Failed to parse questions JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Calculate score from answers
     */
    private int calculateScore(List<QuestionData> questions, List<AnswerData> answers) {
        Map<String, String> correctAnswers = new HashMap<>();
        for (QuestionData q : questions) {
            correctAnswers.put(q.questionId, q.correctAnswer);
            log.info("   📝 Question {}: correct answer = '{}'", q.questionId, q.correctAnswer);
        }

        int score = 0;
        for (AnswerData answer : answers) {
            String correct = correctAnswers.get(answer.questionId);
            boolean isCorrect = correct != null && correct.equals(answer.answer);
            log.info("   📝 Answer Q{}: userAnswer='{}' vs correct='{}' → {}",
                    answer.questionId, answer.answer, correct, isCorrect ? "✓ CORRECT (+10)" : "✗ WRONG");
            if (isCorrect) {
                score += POINTS_PER_CORRECT;
            }
        }
        log.info("   📊 Total score calculated: {}", score);
        return score;
    }

    /**
     * Analyze answer timing to detect bots
     */
    private TimingAnalysis analyzeAnswerTiming(List<AnswerData> answers) {
        if (answers == null || answers.isEmpty()) {
            log.info("   No answers to analyze timing");
            return new TimingAnalysis(false, "");
        }

        int tooFastCount = 0;
        long minTime = Long.MAX_VALUE;
        long totalTime = 0;

        for (AnswerData answer : answers) {
            if (answer.timeToAnswer < MIN_ANSWER_TIME_MS) {
                tooFastCount++;
            }
            minTime = Math.min(minTime, answer.timeToAnswer);
            totalTime += answer.timeToAnswer;
        }

        double avgTime = (double) totalTime / answers.size();
        log.info("   Timing stats: min={}ms, avg={:.1f}ms, tooFast={}/{}",
                minTime, avgTime, tooFastCount, answers.size());

        // Suspicious if >50% answers are too fast
        if (tooFastCount > answers.size() / 2) {
            return new TimingAnalysis(true,
                    String.format("Too many fast answers: %d/%d, min=%dms, avg=%.1fms",
                            tooFastCount, answers.size(), minTime, avgTime));
        }

        // Suspicious if minimum time is impossibly fast (<100ms)
        if (minTime < 100) {
            return new TimingAnalysis(true,
                    String.format("Impossibly fast answer: %dms", minTime));
        }

        return new TimingAnalysis(false, "");
    }

    // ============ Inner Classes ============

    public static class QuestionData {
        public String questionId;
        public String question;
        public String correctAnswer;
        public List<String> choices;
    }

    public static class AnswerData {
        public String questionId;
        public String answer;
        public long timeToAnswer; // milliseconds
    }

    public static class ValidationResult {
        public boolean success;
        public int verifiedScore;
        public String errorCode;
        public String errorMessage;
        public boolean flaggedSuspicious;

        public static ValidationResult success(int score, boolean suspicious) {
            ValidationResult result = new ValidationResult();
            result.success = true;
            result.verifiedScore = score;
            result.flaggedSuspicious = suspicious;
            return result;
        }

        public static ValidationResult error(String code, String message) {
            ValidationResult result = new ValidationResult();
            result.success = false;
            result.errorCode = code;
            result.errorMessage = message;
            return result;
        }
    }

    private static class TimingAnalysis {
        boolean isSuspicious;
        String reason;

        TimingAnalysis(boolean suspicious, String reason) {
            this.isSuspicious = suspicious;
            this.reason = reason;
        }
    }
}
