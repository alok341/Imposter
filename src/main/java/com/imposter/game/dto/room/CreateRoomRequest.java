package com.imposter.game.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to create a new game room.
 * Only the host's name is required - everything else is generated server-side.
 */

public record CreateRoomRequest(

        @NotBlank(message = "Player name is required")
        @Size(min = 2, max = 20, message = "Player name must be between 2 and 20 characters")
        String playerName,

        @Size(max = 30, message = "Display name cannot exceed 30 characters")
        String displayName
) {
    // Convenience method to get the effective name
    public String getEffectiveName() {
        return displayName != null && !displayName.isBlank() ? displayName : playerName;
    }
}
