package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a player ID already exists in a room.
 * This should never happen with UUIDs but serves as a safety check.
 */
public class PlayerAlreadyExistsException extends BaseException {

    public PlayerAlreadyExistsException(String playerId, String roomCode) {
        super(
                String.format("Player '%s' already exists in room '%s'", playerId, roomCode),
                HttpStatus.CONFLICT,
                "PLAYER_ALREADY_EXISTS"
        );
    }
}