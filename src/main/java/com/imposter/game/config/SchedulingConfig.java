package com.imposter.game.config;

import com.imposter.game.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled tasks for room cleanup and maintenance.
 */

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final RoomService roomService;

    /**
     * Clean up expired rooms every 30 minutes.
     * Rooms inactive for more than 2 hours are deleted.
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void cleanupExpiredRooms() {
        log.info("Starting room cleanup task");

        Instant cutoffTime = Instant.now().minus(2, ChronoUnit.HOURS);

        try {
            roomService.cleanupExpiredRooms(cutoffTime);
            log.info("Room cleanup completed");
        } catch (Exception e) {
            log.error("Error during room cleanup", e);
        }
    }

    /**
     * Log active room count every hour for monitoring.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void logActiveRooms() {
        try {
            // This would require a repository call
            // Add when we have monitoring requirements
            log.debug("Room monitoring task executed");
        } catch (Exception e) {
            log.error("Error during room monitoring", e);
        }
    }
}