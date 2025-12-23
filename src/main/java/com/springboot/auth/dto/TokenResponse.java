package com.springboot.auth.dto;

/**
 * Token Response DTO
 * 
 * Trả về cho client sau khi login/refresh thành công
 */
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long accessTokenExpiresIn; // Thời gian hết hạn access token (seconds)
    private long refreshTokenExpiresIn; // Thời gian hết hạn refresh token (seconds)

    public TokenResponse() {
    }

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = 15 * 60; // 15 phút
        this.refreshTokenExpiresIn = 7 * 24 * 60 * 60; // 7 ngày
    }

    public TokenResponse(String accessToken, String refreshToken,
            long accessExpiresIn, long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessExpiresIn;
        this.refreshTokenExpiresIn = refreshExpiresIn;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getAccessTokenExpiresIn() {
        return accessTokenExpiresIn;
    }

    public void setAccessTokenExpiresIn(long accessTokenExpiresIn) {
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public long getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }

    public void setRefreshTokenExpiresIn(long refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}
