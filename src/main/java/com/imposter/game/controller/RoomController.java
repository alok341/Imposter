package com.imposter.game.controller;

import com.imposter.game.dto.common.ApiResponse;
import com.imposter.game.dto.room.*;
import com.imposter.game.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for room management operations.
 * Handles room creation, joining, and information retrieval.
 */

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * Create a new room.
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        log.info("Creating room for player: {}", request.playerName());

        CreateRoomResponse response = roomService.createRoom(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Room created successfully"));
    }

    /**
     * Join an existing room.
     * POST /api/rooms/{roomCode}/join
     */
    @PostMapping("/{roomCode}/join")
    public ResponseEntity<ApiResponse<JoinRoomResponse>> joinRoom(
            @PathVariable String roomCode,
            @Valid @RequestBody JoinRoomRequest request) {

        log.info("Player {} joining room {}", request.playerName(), roomCode);

        JoinRoomResponse response = roomService.joinRoom(roomCode, request);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Joined room successfully"));
    }

    /**
     * Get room information (public data only).
     * GET /api/rooms/{roomCode}
     */
    @GetMapping("/{roomCode}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoom(
            @PathVariable String roomCode) {

        log.debug("Fetching room info for: {}", roomCode);

        RoomResponse response = roomService.getRoom(roomCode);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    /**
     * Reconnect to a room using player ID.
     * GET /api/rooms/player/{playerId}
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByPlayerId(
            @PathVariable String playerId) {

        log.debug("Reconnecting player: {}", playerId);

        var room = roomService.getRoomByPlayerId(playerId);
        RoomResponse response = RoomResponse.fromEntity(room);

        return ResponseEntity
                .ok(ApiResponse.success(response, "Reconnected successfully"));
    }
}