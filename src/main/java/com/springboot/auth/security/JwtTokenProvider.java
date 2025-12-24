package com.springboot.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token Provider - Tạo và xác thực JWT tokens
 * 
 * Sử dụng:
 * - Access Token: 15 phút, dùng cho API calls
 * - Refresh Token: 7 ngày, dùng để lấy access token mới
 */
@Component
public class JwtTokenProvider {

    // Secret key cho JWT (trong production nên lưu ở environment variable)
    private static final String JWT_SECRET = "EduQuizzApp_JWT_Secret_Key_2024_Must_Be_At_Least_256_Bits_Long!";

    // Token expiration times (dễ dàng thay đổi khi chuyển production)
    private static final long ACCESS_TOKEN_EXPIRATION = 1 * 60 * 1000; // 1 phút (testing)
    private static final long REFRESH_TOKEN_EXPIRATION = 2 * 60 * 1000; // 2 phút (testing)

    // Production values (uncomment khi deploy):
    // private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15
    // phút
    // private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;
    // // 7 ngày

    private final SecretKey secretKey;

    public JwtTokenProvider() {
        this.secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Get access token expiration in SECONDS (for API response)
     */
    public long getAccessTokenExpirationSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }

    /**
     * Get refresh token expiration in SECONDS (for API response)
     */
    public long getRefreshTokenExpirationSeconds() {
        return REFRESH_TOKEN_EXPIRATION / 1000;
    }

    /**
     * Generate Access Token
     * 
     * @param userId   User ID
     * @param username Username
     * @param role     User role (USER/ADMIN)
     * @return Access token string
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "access");

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        System.out.println("\n🔐 [JWT] ========== ACCESS TOKEN GENERATED ==========");
        System.out.println("🔐 [JWT] UserId: " + userId);
        System.out.println("🔐 [JWT] Username: " + username);
        System.out.println("🔐 [JWT] Role: " + role);
        System.out.println("🔐 [JWT] Expires: " + expiry);
        System.out.println(
                "🔐 [JWT] Token (first 50 chars): " + token.substring(0, Math.min(50, token.length())) + "...");
        System.out.println("🔐 [JWT] ================================================\n");

        return token;
    }

    /**
     * Generate Refresh Token
     * 
     * @param userId   User ID
     * @param username Username
     * @return Refresh token string
     */
    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        System.out.println("🔄 [JWT] ========== REFRESH TOKEN GENERATED ==========");
        System.out.println("🔄 [JWT] UserId: " + userId);
        System.out.println("🔄 [JWT] Username: " + username);
        System.out.println("🔄 [JWT] Expires: " + expiry + " (7 days)");
        System.out.println("🔄 [JWT] ================================================\n");

        return token;
    }

    /**
     * Validate token
     * 
     * @param token JWT token
     * @return true nếu token hợp lệ
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            String username = getUsernameFromToken(token);
            String tokenType = getTokenType(token);
            System.out.println("✅ [JWT] Token VALID - User: " + username + ", Type: " + tokenType);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("⏰ [JWT] Token EXPIRED: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ [JWT] Token INVALID: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Get user ID from token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    /**
     * Get role from token
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims != null ? (String) claims.get("role") : null;
    }

    /**
     * Get token type (access/refresh)
     */
    public String getTokenType(String token) {
        Claims claims = getClaims(token);
        return claims != null ? (String) claims.get("type") : null;
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims != null && claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Get remaining time until token expires (in milliseconds)
     */
    public long getTokenRemainingTime(String token) {
        Claims claims = getClaims(token);
        if (claims != null) {
            Date expiry = claims.getExpiration();
            return expiry.getTime() - System.currentTimeMillis();
        }
        return 0;
    }

    /**
     * Parse claims from token
     */
    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
