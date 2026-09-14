package com.imposter.game.dto.admin;

import com.imposter.game.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request to create a new word set.
 * Only accessible by admin.
 */

public record CreateWordSetRequest(

        @NotBlank(message = "Normal word is required")
        @Size(min = 2, max = 50, message = "Normal word must be between 2 and 50 characters")
        String normalWord,

        @NotBlank(message = "Imposter word is required")
        @Size(min = 2, max = 50, message = "Imposter word must be between 2 and 50 characters")
        String imposterWord,

        @NotBlank(message = "Category is required")
        @Size(max = 30, message = "Category cannot exceed 30 characters")
        String category,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty
) {
}