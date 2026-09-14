package com.imposter.game.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to join an existing game room.
 * Room code is in the URL path, so only player name is needed here.
 */

public record JoinRoomRequest(

        @NotBlank(message = "Player name is required")
        @Size(min = 2, max = 20, message = "Player name must be between 2 and 20 characters")
        String playerName
) {
}