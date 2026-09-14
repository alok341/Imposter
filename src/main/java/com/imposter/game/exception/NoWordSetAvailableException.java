package com.imposter.game.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when no active word sets are available for selection.
 */
public class NoWordSetAvailableException extends BaseException {

    public NoWordSetAvailableException() {
        super(
                "No active word sets available. Please contact administrator.",
                HttpStatus.SERVICE_UNAVAILABLE,
                "NO_WORD_SETS_AVAILABLE"
        );
    }

    public NoWordSetAvailableException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "NO_WORD_SETS_AVAILABLE");
    }
}