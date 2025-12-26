package com.springboot.game.repository;

import com.springboot.game.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, String> {

    /**
     * Find session by ID
     */
    Optional<GameSession> findBySessionId(String sessionId);

    /**
     * Find all sessions for a user
     */
    List<GameSession> findByUserIdOrderByStartTimeDesc(Long userId);

    /**
     * Find sessions by game type
     */
    List<GameSession> findByGameTypeAndUserId(String gameType, Long userId);

    /**
     * Find suspicious sessions for review
     */
    List<GameSession> findBySuspiciousTrue();

    /**
     * Check if session exists and not submitted
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM GameSession s " +
           "WHERE s.sessionId = :sessionId AND s.submitted = false AND s.expired = false")
    boolean isSessionValid(String sessionId);

    /**
     * Find expired sessions (older than 30 minutes and not submitted)
     */
    @Query("SELECT s FROM GameSession s WHERE s.submitted = false AND s.expired = false " +
           "AND s.startTime < :expiryTime")
    List<GameSession> findExpiredSessions(LocalDateTime expiryTime);

    /**
     * Get user's best score for a game type
     */
    @Query("SELECT MAX(s.verifiedScore) FROM GameSession s " +
           "WHERE s.userId = :userId AND s.gameType = :gameType AND s.verifiedScore IS NOT NULL")
    Optional<Integer> getUserBestScore(Long userId, String gameType);

    /**
     * Count suspicious submissions by user
     */
    long countByUserIdAndSuspiciousTrue(Long userId);
}
