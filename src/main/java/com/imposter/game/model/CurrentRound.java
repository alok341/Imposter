package com.imposter.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the current active round in a game.
 *
 * IMPORTANT: This contains sensitive information that must NEVER be exposed
 * in public API responses. Imposter IDs are private game state.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentRound {

    private int roundNumber;

    private String wordSetId;

    private List<String> imposterIds;

    // Helper method to check if a player is the imposter
    public boolean isImposter(String playerId) {
        return imposterIds != null && imposterIds.contains(playerId);
    }
}