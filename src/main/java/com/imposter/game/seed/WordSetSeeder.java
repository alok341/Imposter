package com.imposter.game.seed;

import com.imposter.game.enums.Difficulty;
import com.imposter.game.model.WordSet;
import com.imposter.game.repository.WordSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

/**
 * Seeds the database with initial word sets on application startup.
 * Only runs if the word_sets collection is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WordSetSeeder implements CommandLineRunner {

    private final WordSetRepository wordSetRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        // Check if word sets already exist
        if (wordSetRepository.count() > 0) {
            log.info("Word sets already exist ({} found). Skipping seeding.",
                    wordSetRepository.count());
            return;
        }

        try {
            seedWordSets();
        } catch (Exception e) {
            log.error("Failed to seed word sets", e);
        }
    }

    private void seedWordSets() throws Exception {
        log.info("Starting word set seeding...");

        // Load JSON file
        ClassPathResource resource = new ClassPathResource("data/seed-words.json");
        InputStream inputStream = resource.getInputStream();

        // Parse JSON to list of temporary DTOs
        List<SeedWord> seedWords = objectMapper.readValue(
                inputStream,
                new TypeReference<List<SeedWord>>() {}
        );

        // Convert to WordSet entities and save
        int count = 0;
        for (SeedWord seedWord : seedWords) {
            WordSet wordSet = WordSet.builder()
                    .normalWord(seedWord.normalWord())
                    .imposterWord(seedWord.imposterWord())
                    .category(seedWord.category())
                    .difficulty(Difficulty.valueOf(seedWord.difficulty()))
                    .active(true)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            wordSetRepository.save(wordSet);
            count++;
        }

        log.info("Successfully seeded {} word sets", count);
    }

    /**
     * Temporary record for JSON deserialization.
     */
    private record SeedWord(
            String normalWord,
            String imposterWord,
            String category,
            String difficulty
    ) {}
}
