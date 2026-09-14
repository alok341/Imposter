package com.imposter.game.dto.websocket;

import com.imposter.game.dto.game.GameStateResponse;

/**
 * Data payload for game-related WebSocket events.
 */

public record GameEventData(
        GameStateResponse gameState,
        int roundNumber,
        int imposterCount
) {
    public static GameEventData from(GameStateResponse state) {
        return new GameEventData(
                state,
                state.currentRound(),
                state.imposterCount()
        );
    }
}