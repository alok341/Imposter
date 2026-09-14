package com.imposter.game.service;

import com.imposter.game.dto.admin.CreateWordSetRequest;
import com.imposter.game.dto.admin.UpdateWordSetRequest;
import com.imposter.game.dto.admin.WordSetResponse;
import com.imposter.game.enums.Difficulty;
import com.imposter.game.exception.InvalidGameStateException;
import com.imposter.game.exception.RoomNotFoundException;
import com.imposter.game.model.WordSet;
import com.imposter.game.repository.WordSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service for admin operations.
 * Handles word set CRUD and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final WordSetRepository wordSetRepository;

    /**
     * Create a new word set.
     */
    @Transactional
    public WordSetResponse createWordSet(CreateWordSetRequest request) {
        // Check for duplicates
        wordSetRepository.findByNormalWordAndImposterWord(
                request.normalWord(),
                request.imposterWord()
        ).ifPresent(existing -> {
            throw new IllegalArgumentException(
                    "Word set already exists: " +
                            request.normalWord() + " / " + request.imposterWord()
            );
        });

        WordSet wordSet = WordSet.builder()
                .normalWord(request.normalWord())
                .imposterWord(request.imposterWord())
                .category(request.category())
                .difficulty(request.difficulty())
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        WordSet saved = wordSetRepository.save(wordSet);
        log.info("Admin created word set: {} / {}",
                saved.getNormalWord(), saved.getImposterWord());

        return WordSetResponse.fromEntity(saved);
    }

    /**
     * Get all word sets with pagination.
     */
    public List<WordSetResponse> getAllWordSets(Pageable pageable) {
        return wordSetRepository.findAll(pageable)
                .stream()
                .map(WordSetResponse::fromEntity)
                .toList();
    }

    /**
     * Get word set by ID.
     */
    public WordSetResponse getWordSet(String id) {
        WordSet wordSet = wordSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Word set not found: " + id));

        return WordSetResponse.fromEntity(wordSet);
    }

    /**
     * Update word set.
     */
    @Transactional
    public WordSetResponse updateWordSet(String id, UpdateWordSetRequest request) {
        WordSet wordSet = wordSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Word set not found: " + id));

        // Update only provided fields
        if (request.normalWord() != null) {
            wordSet.setNormalWord(request.normalWord());
        }
        if (request.imposterWord() != null) {
            wordSet.setImposterWord(request.imposterWord());
        }
        if (request.category() != null) {
            wordSet.setCategory(request.category());
        }
        if (request.difficulty() != null) {
            wordSet.setDifficulty(request.difficulty());
        }
        if (request.active() != null) {
            wordSet.setActive(request.active());
        }

        wordSet.setUpdatedAt(Instant.now());

        WordSet updated = wordSetRepository.save(wordSet);
        log.info("Admin updated word set: {}", id);

        return WordSetResponse.fromEntity(updated);
    }

    /**
     * Delete word set (soft delete).
     */
    @Transactional
    public void deleteWordSet(String id) {
        WordSet wordSet = wordSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Word set not found: " + id));

        // Soft delete - set active to false
        wordSet.setActive(false);
        wordSet.setUpdatedAt(Instant.now());
        wordSetRepository.save(wordSet);

        log.info("Admin deactivated word set: {}", id);
    }

    /**
     * Search word sets.
     */
    public List<WordSetResponse> searchWordSets(String searchTerm) {
        return wordSetRepository.searchWordSets(searchTerm)
                .stream()
                .map(WordSetResponse::fromEntity)
                .toList();
    }

    /**
     * Get word sets by category.
     */
    public List<WordSetResponse> getWordSetsByCategory(String category) {
        return wordSetRepository.findByActiveTrueAndCategory(category)
                .stream()
                .map(WordSetResponse::fromEntity)
                .toList();
    }

    /**
     * Get word sets by difficulty.
     */
    public List<WordSetResponse> getWordSetsByDifficulty(Difficulty difficulty) {
        return wordSetRepository.findByActiveTrueAndDifficulty(difficulty)
                .stream()
                .map(WordSetResponse::fromEntity)
                .toList();
    }

    /**
     * Get word set statistics.
     */
    public WordSetStats getStats() {
        long total = wordSetRepository.count();
        long active = wordSetRepository.countByActiveTrue();
        long inactive = total - active;

        return new WordSetStats(total, active, inactive);
    }

    /**
     * Statistics DTO.
     */
    public record WordSetStats(long total, long active, long inactive) {}
}