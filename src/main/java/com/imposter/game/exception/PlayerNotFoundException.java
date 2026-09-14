package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a player ID doesn't match any player in the room.
 */
public class PlayerNotFoundException extends BaseException {

    public PlayerNotFoundException(String playerId) {
        super(
                String.format("Player with ID '%s' not found", playerId),
                HttpStatus.NOT_FOUND,
                "PLAYER_NOT_FOUND"
        );
    }

    public PlayerNotFoundException(String playerId, String roomCode) {
        super(
                String.format("Player '%s' not found in room '%s'", playerId, roomCode),
                HttpStatus.NOT_FOUND,
                "PLAYER_NOT_FOUND"
        );
    }
}