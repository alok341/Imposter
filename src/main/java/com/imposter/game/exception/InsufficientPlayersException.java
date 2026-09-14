package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when trying to start a game with fewer than minimum required players.
 */
public class InsufficientPlayersException extends BaseException {

    public static final int MIN_PLAYERS = 4;

    public InsufficientPlayersException(int currentPlayers) {
        super(
                String.format("Need at least %d players to start (currently have %d)",
                        MIN_PLAYERS, currentPlayers),
                HttpStatus.BAD_REQUEST,
                "INSUFFICIENT_PLAYERS"
        );
    }

    public InsufficientPlayersException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INSUFFICIENT_PLAYERS");
    }
}