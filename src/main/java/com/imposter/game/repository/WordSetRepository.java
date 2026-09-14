package com.imposter.game.repository;

import com.imposter.game.enums.Difficulty;
import com.imposter.game.model.WordSet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing WordSet documents in MongoDB.
 *
 * Word sets are permanent game content that persists across all game sessions.
 */
@Repository
public interface WordSetRepository extends MongoRepository<WordSet, String> {

    /**
     * Find all active word sets.
     * Used for random word selection during game rounds.
     */
    List<WordSet> findByActiveTrue();

    /**
     * Find active word sets by category.
     * Future feature: Allow hosts to select specific categories.
     */
    List<WordSet> findByActiveTrueAndCategory(String category);

    /**
     * Find active word sets by difficulty.
     * Future feature: Allow hosts to select difficulty level.
     */
    List<WordSet> findByActiveTrueAndDifficulty(Difficulty difficulty);

    /**
     * Find active word sets excluding already used ones.
     * Critical for preventing word repetition within a game session.
     *
     * @param usedIds List of word set IDs already used in this room
     */
    @Query("{ 'active': true, '_id': { $nin: ?0 } }")
    List<WordSet> findActiveWordSetsNotIn(List<String> usedIds);

    /**
     * Count active word sets for validation.
     * Used to check if enough words are available.
     */
    long countByActiveTrue();

    /**
     * Find word set by exact word pair.
     * Used for duplicate detection during admin operations.
     */
    Optional<WordSet> findByNormalWordAndImposterWord(String normalWord, String imposterWord);

    /**
     * Search word sets by text.
     * For admin panel search functionality.
     */
    @Query("{ $or: [ " +
            "{ 'normalWord': { $regex: ?0, $options: 'i' } }, " +
            "{ 'imposterWord': { $regex: ?0, $options: 'i' } }, " +
            "{ 'category': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<WordSet> searchWordSets(String searchTerm);

    /**
     * Find all active word sets paginated.
     * For admin panel listing.
     */
    @Query("{ 'active': true }")
    List<WordSet> findAllActiveWithPagination(org.springframework.data.domain.Pageable pageable);
}