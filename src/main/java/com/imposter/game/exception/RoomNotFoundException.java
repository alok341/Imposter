package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a room with the specified code doesn't exist.
 */
public class RoomNotFoundException extends BaseException {

    public RoomNotFoundException(String roomCode) {
        super(
                String.format("Room with code '%s' not found", roomCode),
                HttpStatus.NOT_FOUND,
                "ROOM_NOT_FOUND"
        );
    }

    public RoomNotFoundException(String message, String roomCode) {
        super(message, HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND");
    }
}