package com.imposter.game.dto.room;

import com.imposter.game.model.Room;
import com.imposter.game.model.Player;

/**
 * Response when a room is created.
 * Contains the essential information the host needs to join and manage the room.
 */

public record CreateRoomResponse(
        String roomCode,
        String playerId,
        boolean isHost,
        String hostName
) {
    public static CreateRoomResponse fromEntity(Room room, Player host) {
        return new CreateRoomResponse(
                room.getRoomCode(),
                host.getPlayerId(),
                true,
                host.getName()
        );
    }
}