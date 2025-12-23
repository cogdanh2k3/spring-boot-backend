package com.springboot.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * 
 * Filter này chạy TRƯỚC mỗi request để:
 * 1. Extract JWT token từ Authorization header
 * 2. Validate token
 * 3. Set Authentication vào SecurityContext
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        System.out.println("\n🔍 [FILTER] ========== REQUEST INTERCEPTED ==========");
        System.out.println("🔍 [FILTER] Method: " + method);
        System.out.println("🔍 [FILTER] Path: " + requestPath);

        try {
            // 1. Lấy token từ header
            String token = extractTokenFromRequest(request);

            if (token == null) {
                System.out.println("🔍 [FILTER] No Bearer token in Authorization header");
            } else {
                System.out.println("🔍 [FILTER] Token found (first 30 chars): "
                        + token.substring(0, Math.min(30, token.length())) + "...");
            }

            // 2. Validate và set authentication nếu token hợp lệ
            if (token != null && jwtTokenProvider.validateToken(token)) {

                // Kiểm tra phải là access token (không phải refresh token)
                String tokenType = jwtTokenProvider.getTokenType(token);
                if (!"access".equals(tokenType)) {
                    // Nếu là refresh token, không cho phép sử dụng như access token
                    System.out.println("❌ [FILTER] Rejected: Using refresh token as access token!");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Invalid token type\"}");
                    return;
                }

                // Lấy thông tin user từ token
                String username = jwtTokenProvider.getUsernameFromToken(token);
                Long userId = jwtTokenProvider.getUserIdFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);

                System.out.println("✅ [FILTER] Authentication SUCCESS");
                System.out.println("✅ [FILTER] User: " + username + " (ID: " + userId + ")");
                System.out.println("✅ [FILTER] Role: " + role);

                // Tạo authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null, // Không cần password
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

                // Thêm userId vào details để có thể truy cập sau
                authentication.setDetails(new JwtUserDetails(userId, username, role));

                // Set vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (token != null) {
                System.out.println("❌ [FILTER] Token validation FAILED");
            }

        } catch (Exception e) {
            // Log error nhưng vẫn tiếp tục filter chain
            // Các endpoint public sẽ không bị ảnh hưởng
            System.out.println("❌ [FILTER] JWT Authentication EXCEPTION: " + e.getMessage());
            logger.error("JWT Authentication failed: " + e.getMessage());
        }

        System.out.println("🔍 [FILTER] ================================================\n");

        // Tiếp tục filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token từ Authorization header
     * Format: "Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Inner class để lưu user details
     */
    public static class JwtUserDetails {
        private final Long userId;
        private final String username;
        private final String role;

        public JwtUserDetails(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }
}
