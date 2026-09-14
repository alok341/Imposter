package com.imposter.game.dto.game;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Request to start a game from the host.
 * Imposter count is optional - defaults based on player count if not provided.
 */

public record StartGameRequest(

        @Min(value = 1, message = "Imposter count must be at least 1")
        @Max(value = 3, message = "Imposter count cannot exceed 3")
        Integer imposterCount
) {
    // No additional validation needed - services will validate based on player count
}