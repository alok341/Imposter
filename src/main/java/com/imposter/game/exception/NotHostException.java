package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a non-host player attempts a host-only operation.
 */
public class NotHostException extends BaseException {

    public NotHostException(String playerId) {
        super(
                String.format("Player '%s' is not the host of this room", playerId),
                HttpStatus.FORBIDDEN,
                "NOT_HOST"
        );
    }

    public NotHostException(String playerId, String operation) {
        super(
                String.format("Player '%s' is not authorized to perform: %s", playerId, operation),
                HttpStatus.FORBIDDEN,
                "NOT_HOST"
        );
    }
}
