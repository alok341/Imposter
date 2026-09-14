package com.imposter.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration settings for a game room.
 * Set by the host before the game starts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSettings {

    private int playerLimit;

    private int imposterCount;

    // Future settings can be added here:
    // private String category;
    // private Difficulty difficulty;
    // private boolean allowSpectators;

    // Default values
    public static GameSettings defaultSettings() {
        return GameSettings.builder()
                .playerLimit(12)
                .imposterCount(1)
                .build();
    }
}