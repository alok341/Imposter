package com.imposter.game.dto.game;

import com.imposter.game.enums.GameStatus;
import com.imposter.game.model.Room;

public record GameStateResponse(
        GameStatus status,
        int currentRound,
        int totalPlayers,
        int imposterCount,
        String lastUpdated  // Changed from Instant to String
) {
    public static GameStateResponse fromEntity(Room room) {
        int currentRound = room.getCurrentRound() != null
                ? room.getCurrentRound().getRoundNumber()
                : 0;

        return new GameStateResponse(
                room.getStatus(),
                currentRound,
                room.getPlayers().size(),
                room.getSettings().getImposterCount(),
                java.time.Instant.now().toString()
        );
    }
}