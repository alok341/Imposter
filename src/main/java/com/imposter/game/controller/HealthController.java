package com.imposter.game.controller;

import com.imposter.game.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple health check endpoint for monitoring.
 */

@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * Basic health check.
     * GET /api/health
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", Instant.now());
        healthInfo.put("service", "imposter-game");

        return ResponseEntity.ok(ApiResponse.success(healthInfo, "Service is healthy"));
    }
}