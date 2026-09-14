package com.imposter.game.service;

import com.imposter.game.exception.NoWordSetAvailableException;
import com.imposter.game.model.Room;
import com.imposter.game.model.WordSet;
import com.imposter.game.repository.WordSetRepository;
import com.imposter.game.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordService {

    private final WordSetRepository wordSetRepository;
    private final RandomUtil randomUtil;

    /**
     * Select a random word set for the current round.
     */
    public WordSet selectWordSet(Room room) {
        log.debug("Selecting word set for room {}", room.getRoomCode());

        // Get all active word sets
        List<WordSet> allActiveWordSets = wordSetRepository.findByActiveTrue();
        log.debug("Total active word sets: {}", allActiveWordSets.size());

        if (allActiveWordSets.isEmpty()) {
            throw new NoWordSetAvailableException();
        }

        // Get available word sets (not used in this room)
        List<WordSet> availableWordSets;

        if (room.getUsedWordSetIds() == null || room.getUsedWordSetIds().isEmpty()) {
            availableWordSets = allActiveWordSets;
        } else {
            availableWordSets = allActiveWordSets.stream()
                    .filter(ws -> !room.getUsedWordSetIds().contains(ws.getId()))
                    .toList();
        }

        log.debug("Available word sets: {}", availableWordSets.size());

        // If all words used, reset
        if (availableWordSets.isEmpty()) {
            log.info("All word sets used in room {}, resetting", room.getRoomCode());
            room.getUsedWordSetIds().clear();
            availableWordSets = allActiveWordSets;
        }

        // Select random word set
        WordSet selected = randomUtil.selectRandom(availableWordSets);

        if (selected == null) {
            throw new NoWordSetAvailableException("Failed to select word set");
        }

        // Add to used list
        if (room.getUsedWordSetIds() == null) {
            room.setUsedWordSetIds(new java.util.ArrayList<>());
        }
        room.getUsedWordSetIds().add(selected.getId());

        log.debug("Selected word set: {} - {} / {}",
                selected.getId(), selected.getNormalWord(), selected.getImposterWord());

        return selected;
    }

    /**
     * Get a word set by ID.
     */
    public WordSet getWordSetById(String wordSetId) {
        return wordSetRepository.findById(wordSetId)
                .orElseThrow(() -> new NoWordSetAvailableException("Word set not found: " + wordSetId));
    }

    /**
     * Check if there are enough word sets available.
     */
    public boolean hasAvailableWordSets() {
        long count = wordSetRepository.countByActiveTrue();
        log.debug("Active word set count: {}", count);
        return count > 0;
    }

    /**
     * Get total count of active word sets.
     */
    public long getActiveWordSetCount() {
        return wordSetRepository.countByActiveTrue();
    }
}