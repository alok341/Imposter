package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a room code format is invalid.
 */
public class InvalidRoomCodeException extends BaseException {

    public InvalidRoomCodeException(String roomCode) {
        super(
                String.format("Invalid room code format: '%s'", roomCode),
                HttpStatus.BAD_REQUEST,
                "INVALID_ROOM_CODE"
        );
    }
}