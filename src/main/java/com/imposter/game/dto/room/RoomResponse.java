package com.imposter.game.dto.room;

import com.imposter.game.dto.player.PlayerResponse;
import com.imposter.game.enums.GameStatus;
import com.imposter.game.model.Room;

import java.util.List;

/**
 * Public room information that's safe to share with all players.
 *
 * CRITICAL: This DTO must NEVER include:
 * - Current round's imposter IDs
 * - Word set information
 * - Any player's role
 * - Used word set IDs (internal tracking data)
 */

public record RoomResponse(
        String roomCode,
        GameStatus status,
        List<PlayerResponse> players,
        int playerCount,
        int maxPlayers,
        int currentRoundNumber,
        int imposterCount,
        String hostPlayerId
) {
    public static RoomResponse fromEntity(Room room) {
        List<PlayerResponse> playerResponses = room.getPlayers().stream()
                .map(PlayerResponse::fromEntity)
                .toList();

        int currentRound = room.getCurrentRound() != null
                ? room.getCurrentRound().getRoundNumber()
                : 0;

        return new RoomResponse(
                room.getRoomCode(),
                room.getStatus(),
                playerResponses,
                room.getPlayers().size(),
                room.getSettings().getPlayerLimit(),
                currentRound,
                room.getSettings().getImposterCount(),
                room.getHostPlayerId()
        );
    }
}