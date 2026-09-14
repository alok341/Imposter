package com.imposter.game.service;

import com.imposter.game.dto.room.*;
import com.imposter.game.enums.GameStatus;
import com.imposter.game.exception.*;
import com.imposter.game.model.Player;
import com.imposter.game.model.Room;
import com.imposter.game.repository.RoomRepository;
import com.imposter.game.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service for managing game rooms.
 * Handles room creation, joining, and state management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final PlayerService playerService;
    private final RoomCodeGenerator roomCodeGenerator;
    private final WebSocketNotificationService notificationService;
    /**
     * Create a new room with a host player.
     */
    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        // Create host player
        Player host = playerService.createHostPlayer(request.getEffectiveName());

        // Generate unique room code
        String roomCode = roomCodeGenerator.generateCode(
                code -> roomRepository.existsByRoomCode(code)
        );

        // Create room
        Room room = Room.createRoom(roomCode, host.getPlayerId(), host);

        // Save to database
        roomRepository.save(room);

        log.info("Room {} created by {}", roomCode, host.getName());

        return CreateRoomResponse.fromEntity(room, host);
    }

    /**
     * Join an existing room.
     */
    @Transactional
    public JoinRoomResponse joinRoom(String roomCode, JoinRoomRequest request) {
        // Validate room code format
        validateRoomCode(roomCode);

        // Find room
        Room room = getRoomByCode(roomCode);

        // Check if room can be joined
        if (room.getStatus() != GameStatus.WAITING) {
            throw new InvalidGameStateException(
                    "Cannot join room because game has already started"
            );
        }

        // Check room capacity
        if (room.isFull()) {
            throw new RoomFullException(
                    room.getRoomCode(),
                    room.getPlayers().size(),
                    room.getSettings().getPlayerLimit()
            );
        }

        // Create new player
        Player player = playerService.createPlayer(request.playerName());

        // Add to room
        playerService.addPlayerToRoom(room, player);

        // Save room
        roomRepository.save(room);

        // Send WebSocket notification
        notificationService.notifyPlayerJoined(room, player);

        log.info("Player {} joined room {}", player.getName(), roomCode);

        return JoinRoomResponse.fromEntity(room, player);
    }

    /**
     * Get room information (public data only).
     */
    public RoomResponse getRoom(String roomCode) {
        validateRoomCode(roomCode);
        Room room = getRoomByCode(roomCode);
        return RoomResponse.fromEntity(room);
    }

    /**
     * Get room entity by code.
     */
    public Room getRoomByCode(String roomCode) {
        return roomRepository.findByRoomCode(roomCode.toUpperCase())
                .orElseThrow(() -> new RoomNotFoundException(roomCode));
    }

    /**
     * Get room by player ID (for reconnection).
     */
    public Room getRoomByPlayerId(String playerId) {
        return roomRepository.findActiveRoomByPlayerId(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    /**
     * Update room activity timestamp.
     */
    public void updateRoomActivity(Room room) {
        room.setLastActivityAt(Instant.now());
        roomRepository.save(room);
    }

    /**
     * Validate room code format.
     */
    private void validateRoomCode(String roomCode) {
        if (roomCode == null || roomCode.trim().isEmpty()) {
            throw new InvalidRoomCodeException(roomCode);
        }

        String normalizedCode = roomCode.trim().toUpperCase();
        if (!normalizedCode.matches("[A-HJ-NP-Z2-9]{5}")) {
            throw new InvalidRoomCodeException(roomCode);
        }
    }

    /**
     * Validate that a player is the host of the room.
     */
    public void validateHost(Room room, String playerId) {
        if (!room.isHost(playerId)) {
            throw new NotHostException(playerId);
        }
    }

    /**
     * Clean up expired rooms.
     * Called by scheduled task.
     */
    @Transactional
    public void cleanupExpiredRooms(Instant cutoffTime) {
        List<Room> expiredRooms = roomRepository.findByLastActivityAtBefore(cutoffTime);

        for (Room room : expiredRooms) {
            log.info("Cleaning up expired room {}", room.getRoomCode());
            roomRepository.delete(room);
        }

        if (!expiredRooms.isEmpty()) {
            log.info("Cleaned up {} expired rooms", expiredRooms.size());
        }
    }
    /**
     * Remove player from room (with notification).
     */
    @Transactional
    public void removePlayerFromRoom(Room room, String playerId) {
        Player player = playerService.getPlayerFromRoom(room, playerId);
        playerService.removePlayerFromRoom(room, playerId);

        // Save room
        roomRepository.save(room);

        // Send WebSocket notification
        notificationService.notifyPlayerLeft(room, player);
    }
}