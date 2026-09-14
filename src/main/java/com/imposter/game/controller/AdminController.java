package com.imposter.game.controller;

import com.imposter.game.dto.admin.CreateWordSetRequest;
import com.imposter.game.dto.admin.UpdateWordSetRequest;
import com.imposter.game.dto.admin.WordSetResponse;
import com.imposter.game.dto.common.ApiResponse;
import com.imposter.game.enums.Difficulty;
import com.imposter.game.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin operations.
 * Handles word set CRUD and management.
 *
 * SECURITY: All endpoints require admin authentication.
 */

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * Create a new word set.
     * POST /api/admin/word-sets
     */
    @PostMapping("/word-sets")
    public ResponseEntity<ApiResponse<WordSetResponse>> createWordSet(
            @Valid @RequestBody CreateWordSetRequest request) {

        log.info("Admin creating word set: {} / {}",
                request.normalWord(), request.imposterWord());

        WordSetResponse response = adminService.createWordSet(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Word set created"));
    }

    /**
     * Get all word sets.
     * GET /api/admin/word-sets?page=0&size=20
     */
    @GetMapping("/word-sets")
    public ResponseEntity<ApiResponse<List<WordSetResponse>>> getAllWordSets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<WordSetResponse> response = adminService.getAllWordSets(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get word set by ID.
     * GET /api/admin/word-sets/{id}
     */
    @GetMapping("/word-sets/{id}")
    public ResponseEntity<ApiResponse<WordSetResponse>> getWordSet(
            @PathVariable String id) {

        WordSetResponse response = adminService.getWordSet(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update word set.
     * PUT /api/admin/word-sets/{id}
     */
    @PutMapping("/word-sets/{id}")
    public ResponseEntity<ApiResponse<WordSetResponse>> updateWordSet(
            @PathVariable String id,
            @Valid @RequestBody UpdateWordSetRequest request) {

        log.info("Admin updating word set: {}", id);

        WordSetResponse response = adminService.updateWordSet(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Word set updated"));
    }

    /**
     * Delete word set (soft delete).
     * DELETE /api/admin/word-sets/{id}
     */
    @DeleteMapping("/word-sets/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWordSet(
            @PathVariable String id) {

        log.info("Admin deleting word set: {}", id);

        adminService.deleteWordSet(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Word set deleted"));
    }

    /**
     * Search word sets.
     * GET /api/admin/word-sets/search?q=pizza
     */
    @GetMapping("/word-sets/search")
    public ResponseEntity<ApiResponse<List<WordSetResponse>>> searchWordSets(
            @RequestParam String q) {

        List<WordSetResponse> response = adminService.searchWordSets(q);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get word sets by category.
     * GET /api/admin/word-sets/category/{category}
     */
    @GetMapping("/word-sets/category/{category}")
    public ResponseEntity<ApiResponse<List<WordSetResponse>>> getByCategory(
            @PathVariable String category) {

        List<WordSetResponse> response = adminService.getWordSetsByCategory(category);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get word sets by difficulty.
     * GET /api/admin/word-sets/difficulty/{difficulty}
     */
    @GetMapping("/word-sets/difficulty/{difficulty}")
    public ResponseEntity<ApiResponse<List<WordSetResponse>>> getByDifficulty(
            @PathVariable Difficulty difficulty) {

        List<WordSetResponse> response = adminService.getWordSetsByDifficulty(difficulty);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get word set statistics.
     * GET /api/admin/word-sets/stats
     */
    @GetMapping("/word-sets/stats")
    public ResponseEntity<ApiResponse<AdminService.WordSetStats>> getStats() {

        AdminService.WordSetStats stats = adminService.getStats();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}