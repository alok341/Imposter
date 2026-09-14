package com.imposter.game.model;

import com.imposter.game.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Represents a pair of words used in the game.
 *
 * normalWord = the word most players receive
 * imposterWord = the word the imposter receives
 *
 * These words should be similar enough to make the game interesting,
 * but different enough to be identifiable through discussion.
 */
@Document(collection = "word_sets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "idx_active_difficulty", def = "{'active': 1, 'difficulty': 1}")
@CompoundIndex(name = "idx_active_category", def = "{'active': 1, 'category': 1}")
public class WordSet {

    @Id
    private String id;

    @Indexed
    private String normalWord;

    @Indexed
    private String imposterWord;

    private String category;

    private Difficulty difficulty;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;
}