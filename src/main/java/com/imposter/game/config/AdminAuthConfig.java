package com.imposter.game.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@Slf4j
public class AdminAuthConfig extends OncePerRequestFilter {

    @Value("${app.admin.api-key:admin-secret-key}")
    private String adminApiKey;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // ✅ ALWAYS let CORS preflight through — Spring's CORS filter handles it
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Only protect admin endpoints
        if (request.getRequestURI().startsWith("/api/admin")) {
            String apiKey = request.getHeader("X-Admin-Key");

            if (apiKey == null || !apiKey.equals(adminApiKey)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                // ✅ Add CORS headers so the browser can read the 401 body
                String origin = request.getHeader("Origin");
                if (origin != null) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                }
                response.getWriter().write(
                        "{\"success\":false,\"error\":\"UNAUTHORIZED\",\"message\":\"Invalid admin credentials\"}"
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}