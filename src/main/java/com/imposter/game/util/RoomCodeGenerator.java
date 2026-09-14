package com.imposter.game.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates unique, easy-to-type room codes.
 *
 * Room codes are:
 * - 5 characters long
 * - Uppercase letters and numbers
 * - Exclude ambiguous characters (0, O, 1, I) to prevent confusion
 * - Generated using SecureRandom for unpredictability
 */

@Component
public class RoomCodeGenerator {

    // Exclude ambiguous characters: 0, O, 1, I
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Cache for recently generated codes to prevent immediate reuse
    private final Set<String> recentlyGenerated = new HashSet<>();
    private static final int CACHE_SIZE = 1000;

    /**
     * Generate a new unique room code.
     *
     * @param existsChecker Function to check if code already exists in database
     * @return A unique room code
     */
    public String generateCode(CodeExistsChecker existsChecker) {
        String code;
        int attempts = 0;
        final int MAX_ATTEMPTS = 10;

        do {
            code = generateRandomCode();
            attempts++;

            // Prevent infinite loop
            if (attempts >= MAX_ATTEMPTS) {
                throw new IllegalStateException("Unable to generate unique room code");
            }
        } while (recentlyGenerated.contains(code) || existsChecker.exists(code));

        // Add to recently generated set
        recentlyGenerated.add(code);

        // Keep cache size manageable
        if (recentlyGenerated.size() > CACHE_SIZE) {
            recentlyGenerated.clear();
        }

        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Functional interface for checking code existence.
     * Allows testing without database dependency.
     */
    @FunctionalInterface
    public interface CodeExistsChecker {
        boolean exists(String code);
    }
}