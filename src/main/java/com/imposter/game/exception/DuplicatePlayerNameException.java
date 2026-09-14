package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a player attempts to join with a name already in use.
 */
public class DuplicatePlayerNameException extends BaseException {

    public DuplicatePlayerNameException(String playerName, String roomCode) {
        super(
                String.format("Player name '%s' is already taken in room '%s'", playerName, roomCode),
                HttpStatus.CONFLICT,
                "DUPLICATE_PLAYER_NAME"
        );
    }
}