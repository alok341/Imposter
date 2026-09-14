package com.imposter.game.dto.player;

import com.imposter.game.model.Player;

/**
 * Public representation of a player.
 * This is safe to expose in room responses and WebSocket events.
 *
 * IMPORTANT: Never include role or word information here.
 */

public record PlayerResponse(
        String playerId,
        String name,
        boolean isHost,
        boolean connected
) {
    // Static factory method to convert from entity
    public static PlayerResponse fromEntity(Player player) {
        return new PlayerResponse(
                player.getPlayerId(),
                player.getName(),
                player.isHost(),
                player.isConnected()
        );
    }
}