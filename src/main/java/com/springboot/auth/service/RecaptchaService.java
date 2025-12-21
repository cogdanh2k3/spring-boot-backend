package com.springboot.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    @Value("${recaptcha.verify.url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Verify reCAPTCHA token with Google API
     * @param token reCAPTCHA token from frontend
     * @return true if valid, false otherwise
     */
    public boolean verifyToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            // Build request parameters
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", token);

            // Send POST request to Google
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                verifyUrl,
                request,
                RecaptchaResponse.class
            );

            RecaptchaResponse recaptchaResponse = response.getBody();
            
            if (recaptchaResponse != null && recaptchaResponse.isSuccess()) {
                System.out.println("✅ reCAPTCHA verification SUCCESS");
                return true;
            } else {
                System.out.println("❌ reCAPTCHA verification FAILED");
                if (recaptchaResponse != null) {
                    System.out.println("Error codes: " + recaptchaResponse.getErrorCodes());
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ reCAPTCHA verification exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Response model from Google reCAPTCHA API
    private static class RecaptchaResponse {
        private boolean success;
        
        @JsonProperty("challenge_ts")
        private String challengeTs;
        
        private String hostname;
        
        @JsonProperty("error-codes")
        private String[] errorCodes;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getChallengeTs() { return challengeTs; }
        public void setChallengeTs(String challengeTs) { this.challengeTs = challengeTs; }
        
        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        
        public String[] getErrorCodes() { return errorCodes; }
        public void setErrorCodes(String[] errorCodes) { this.errorCodes = errorCodes; }
    }
}