package com.imposter.game.dto.websocket;

import com.imposter.game.dto.player.PlayerResponse;

/**
 * Data payload for player-related WebSocket events.
 */

public record PlayerEventData(
        PlayerResponse player,
        int currentPlayerCount,
        int maxPlayers
) {
    public static PlayerEventData from(PlayerResponse player, int currentCount, int maxPlayers) {
        return new PlayerEventData(player, currentCount, maxPlayers);
    }
}
