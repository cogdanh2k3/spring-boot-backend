package com.springboot.game.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * GameSession Entity - Lưu trữ thông tin phiên chơi game
 * 
 * Mỗi session đại diện cho một lần chơi từ đầu đến khi submit
 * Server lưu câu hỏi + đáp án đúng để verify khi client submit
 */
@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @Column(length = 64)
    private String sessionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String gameType; // "quizgame", "batchu", "contest"

    @Column(length = 50)
    private String levelId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean submitted = false;

    @Column(nullable = false)
    private boolean expired = false;

    // Lưu câu hỏi và đáp án đúng dưới dạng JSON
    // Sử dụng EAGER fetch và VARCHAR thay vì LOB để tránh lỗi "Unable to access lob
    // stream"
    @Column(columnDefinition = "LONGTEXT")
    @Basic(fetch = FetchType.EAGER)
    private String questionsJson;

    // Score được server tính toán (null nếu chưa submit)
    private Integer verifiedScore;

    // Client score để so sánh
    private Integer clientScore;

    // Signature từ client
    @Column(length = 256)
    private String signature;

    // Timing data để detect bot
    @Column(columnDefinition = "LONGTEXT")
    @Basic(fetch = FetchType.EAGER)
    private String answersJson;

    // Flag nếu phát hiện hành vi đáng ngờ
    private boolean suspicious = false;

    @Column(length = 500)
    private String suspiciousReason;

    // Constructors
    public GameSession() {
    }

    public GameSession(String sessionId, Long userId, String gameType, String levelId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.gameType = gameType;
        this.levelId = levelId;
        this.startTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }

    public Integer getVerifiedScore() {
        return verifiedScore;
    }

    public void setVerifiedScore(Integer verifiedScore) {
        this.verifiedScore = verifiedScore;
    }

    public Integer getClientScore() {
        return clientScore;
    }

    public void setClientScore(Integer clientScore) {
        this.clientScore = clientScore;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAnswersJson() {
        return answersJson;
    }

    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    public boolean isSuspicious() {
        return suspicious;
    }

    public void setSuspicious(boolean suspicious) {
        this.suspicious = suspicious;
    }

    public String getSuspiciousReason() {
        return suspiciousReason;
    }

    public void setSuspiciousReason(String suspiciousReason) {
        this.suspiciousReason = suspiciousReason;
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "sessionId='" + sessionId + '\'' +
                ", userId=" + userId +
                ", gameType='" + gameType + '\'' +
                ", levelId='" + levelId + '\'' +
                ", submitted=" + submitted +
                ", verifiedScore=" + verifiedScore +
                ", suspicious=" + suspicious +
                '}';
    }
}
