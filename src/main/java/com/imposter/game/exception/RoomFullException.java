package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to join a room that has reached its player limit.
 */
public class RoomFullException extends BaseException {

    public RoomFullException(String roomCode, int currentPlayers, int maxPlayers) {
        super(
                String.format("Room %s is full (%d/%d players)", roomCode, currentPlayers, maxPlayers),
                HttpStatus.CONFLICT,
                "ROOM_FULL"
        );
    }
}