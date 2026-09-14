package com.imposter.game.controller;

import com.imposter.game.dto.common.ApiResponse;
import com.imposter.game.dto.game.AssignmentResponse;
import com.imposter.game.dto.game.GameStateResponse;
import com.imposter.game.dto.game.StartGameRequest;
import com.imposter.game.service.GameService;
import com.imposter.game.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for game operations.
 * Handles game lifecycle, rounds, and player assignments.
 *
 * SECURITY: Assignment endpoint returns private data - must validate player ownership.
 */

@RestController
@RequestMapping("/api/rooms/{roomCode}")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final RoomService roomService;  // Add this

    /**
     * Start the game.
     * POST /api/rooms/{roomCode}/start
     *
     * Headers: X-Player-Id: {hostPlayerId}
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<GameStateResponse>> startGame(
            @PathVariable String roomCode,
            @RequestHeader("X-Player-Id") String playerId,
            @Valid @RequestBody(required = false) StartGameRequest request) {

        log.info("Starting game in room {} by player {}", roomCode, playerId);

        Integer imposterCount = request != null ? request.imposterCount() : null;

        GameStateResponse response = gameService.startGame(roomCode, playerId, imposterCount);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Game started successfully"));
    }

    /**
     * Get player's private assignment.
     * GET /api/rooms/{roomCode}/players/{playerId}/assignment
     *
     * SECURITY: This endpoint returns private game information.
     * The playerId in URL must match the actual requesting player.
     */
    @GetMapping("/players/{playerId}/assignment")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
            @PathVariable String roomCode,
            @PathVariable String playerId) {

        log.debug("Getting assignment for player {} in room {}", playerId, roomCode);

        AssignmentResponse response = gameService.getAssignment(roomCode, playerId);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Assignment retrieved"));
    }

    /**
     * Start next round.
     * POST /api/rooms/{roomCode}/next-round
     *
     * Headers: X-Player-Id: {hostPlayerId}
     */
    @PostMapping("/next-round")
    public ResponseEntity<ApiResponse<GameStateResponse>> nextRound(
            @PathVariable String roomCode,
            @RequestHeader("X-Player-Id") String playerId) {

        log.info("Starting next round in room {} by player {}", roomCode, playerId);

        GameStateResponse response = gameService.nextRound(roomCode, playerId);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Next round started"));
    }

    /**
     * End the game.
     * POST /api/rooms/{roomCode}/end
     *
     * Headers: X-Player-Id: {hostPlayerId}
     */
    @PostMapping("/end")
    public ResponseEntity<ApiResponse<GameStateResponse>> endGame(
            @PathVariable String roomCode,
            @RequestHeader("X-Player-Id") String playerId) {

        log.info("Ending game in room {} by player {}", roomCode, playerId);

        GameStateResponse response = gameService.endGame(roomCode, playerId);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Game ended"));
    }

    /**
     * Get current game state (public information only).
     * GET /api/rooms/{roomCode}/game-state
     */
    @GetMapping("/game-state")
    public ResponseEntity<ApiResponse<GameStateResponse>> getGameState(
            @PathVariable String roomCode) {

        log.debug("Getting game state for room {}", roomCode);

        // Fix: Use roomService instead of gameService
        var room = roomService.getRoomByCode(roomCode);
        GameStateResponse response = GameStateResponse.fromEntity(room);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }
}