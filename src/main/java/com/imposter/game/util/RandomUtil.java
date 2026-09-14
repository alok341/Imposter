package com.imposter.game.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility class for random selection operations.
 * Uses SecureRandom for cryptographic-quality randomness.
 */

@Component
public class RandomUtil {

    private final Random random = new SecureRandom();

    /**
     * Select a random element from a list.
     *
     * @param list Source list
     * @return Random element, or null if list is empty
     */
    public <T> T selectRandom(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    /**
     * Select multiple random elements from a list.
     * Ensures no duplicate selections.
     *
     * @param list Source list
     * @param count Number of elements to select
     * @return List of selected elements
     * @throws IllegalArgumentException if count > list size or count < 0
     */
    public <T> List<T> selectRandomMultiple(List<T> list, int count) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        if (count > list.size()) {
            throw new IllegalArgumentException(
                    "Cannot select " + count + " elements from list of size " + list.size()
            );
        }

        if (count == 0) {
            return Collections.emptyList();
        }

        // Create a shuffled copy to avoid modifying original list
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, random);

        return new ArrayList<>(shuffled.subList(0, count));
    }

    /**
     * Shuffle a list in-place.
     * Uses Fisher-Yates algorithm with SecureRandom.
     *
     * @param list List to shuffle
     */
    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, random);
    }

    /**
     * Generate a random integer between min and max (inclusive).
     *
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return Random integer between min and max
     */
    public int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Min cannot be greater than max");
        }
        return random.nextInt(max - min + 1) + min;
    }
}