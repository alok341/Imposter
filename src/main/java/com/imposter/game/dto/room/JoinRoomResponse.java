package com.imposter.game.dto.room;

import com.imposter.game.model.Room;
import com.imposter.game.model.Player;

/**
 * Response when a player joins a room.
 */

public record JoinRoomResponse(
        String roomCode,
        String playerId,
        boolean isHost,
        String playerName,
        int currentPlayerCount,
        int maxPlayers
) {
    public static JoinRoomResponse fromEntity(Room room, Player player) {
        return new JoinRoomResponse(
                room.getRoomCode(),
                player.getPlayerId(),
                player.isHost(),
                player.getName(),
                room.getPlayers().size(),
                room.getSettings().getPlayerLimit()
        );
    }
}