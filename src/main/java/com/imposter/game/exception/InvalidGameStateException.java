package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an operation is attempted in an invalid game state.
 * Example: Starting a game that's already in progress.
 */
public class InvalidGameStateException extends BaseException {

    public InvalidGameStateException(String message) {
        super(message, HttpStatus.CONFLICT, "INVALID_GAME_STATE");
    }

    public InvalidGameStateException(String currentState, String requiredState, String operation) {
        super(
                String.format("Cannot %s when game is in %s state (requires %s)",
                        operation, currentState, requiredState),
                HttpStatus.CONFLICT,
                "INVALID_GAME_STATE"
        );
    }
}