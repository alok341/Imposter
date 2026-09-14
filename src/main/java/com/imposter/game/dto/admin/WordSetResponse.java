package com.imposter.game.dto.admin;

import com.imposter.game.enums.Difficulty;
import com.imposter.game.model.WordSet;

public record WordSetResponse(
        String id,
        String normalWord,
        String imposterWord,
        String category,
        Difficulty difficulty,
        boolean active,
        String createdAt,  // Changed from Instant to String
        String updatedAt   // Changed from Instant to String
) {
    public static WordSetResponse fromEntity(WordSet wordSet) {
        return new WordSetResponse(
                wordSet.getId(),
                wordSet.getNormalWord(),
                wordSet.getImposterWord(),
                wordSet.getCategory(),
                wordSet.getDifficulty(),
                wordSet.isActive(),
                wordSet.getCreatedAt() != null ? wordSet.getCreatedAt().toString() : null,
                wordSet.getUpdatedAt() != null ? wordSet.getUpdatedAt().toString() : null
        );
    }
}