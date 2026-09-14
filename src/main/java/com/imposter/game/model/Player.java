package com.imposter.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents a temporary player in a game room.
 * Players are anonymous and identified only by their UUID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private String playerId;

    private String name;

    private boolean isHost;

    private boolean connected;

    private Instant joinedAt;

    private Instant lastSeenAt;
}