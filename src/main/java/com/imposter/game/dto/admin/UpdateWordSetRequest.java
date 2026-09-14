package com.imposter.game.dto.admin;

import com.imposter.game.enums.Difficulty;
import jakarta.validation.constraints.Size;

/**
 * Request to update an existing word set.
 * Only accessible by admin.
 */

public record UpdateWordSetRequest(

        @Size(min = 2, max = 50, message = "Normal word must be between 2 and 50 characters")
        String normalWord,

        @Size(min = 2, max = 50, message = "Imposter word must be between 2 and 50 characters")
        String imposterWord,

        @Size(max = 30, message = "Category cannot exceed 30 characters")
        String category,

        Difficulty difficulty,

        Boolean active
) {
}