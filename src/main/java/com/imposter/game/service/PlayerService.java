package com.imposter.game.service;

import com.imposter.game.exception.DuplicatePlayerNameException;
import com.imposter.game.exception.PlayerAlreadyExistsException;
import com.imposter.game.exception.PlayerNotFoundException;
import com.imposter.game.model.Player;
import com.imposter.game.model.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for managing players within game rooms.
 * Handles player creation, validation, and state management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    /**
     * Create a new player with a unique UUID.
     * The UUID is generated server-side and never trusted from the client.
     */
    public Player createPlayer(String name) {
        return Player.builder()
                .playerId(UUID.randomUUID().toString())
                .name(name.trim())
                .isHost(false)
                .connected(true)
                .joinedAt(Instant.now())
                .lastSeenAt(Instant.now())
                .build();
    }

    /**
     * Create a host player for a new room.
     * Host has special privileges for game management.
     */
    public Player createHostPlayer(String name) {
        Player host = createPlayer(name);
        host.setHost(true);
        return host;
    }

    /**
     * Validate and add a player to a room.
     * Checks for duplicate names and room capacity.
     */
    public void addPlayerToRoom(Room room, Player player) {
        // Check if player ID already exists (shouldn't happen with UUIDs)
        if (room.getPlayerById(player.getPlayerId()) != null) {
            throw new PlayerAlreadyExistsException(player.getPlayerId(), room.getRoomCode());
        }

        // Check for duplicate names (case-insensitive)
        boolean nameExists = room.getPlayers().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(player.getName()));

        if (nameExists) {
            throw new DuplicatePlayerNameException(player.getName(), room.getRoomCode());
        }

        // Add player to room
        room.getPlayers().add(player);
        room.setLastActivityAt(Instant.now());

        log.debug("Player {} joined room {}", player.getName(), room.getRoomCode());
    }

    /**
     * Get a player from a room by ID.
     * Throws exception if player not found.
     */
    public Player getPlayerFromRoom(Room room, String playerId) {
        Player player = room.getPlayerById(playerId);
        if (player == null) {
            throw new PlayerNotFoundException(playerId, room.getRoomCode());
        }
        return player;
    }

    /**
     * Update player connection status.
     * Used for WebSocket connect/disconnect events.
     */
    public void updatePlayerConnection(Room room, String playerId, boolean connected) {
        Player player = getPlayerFromRoom(room, playerId);
        player.setConnected(connected);
        player.setLastSeenAt(Instant.now());
        room.setLastActivityAt(Instant.now());

        log.debug("Player {} connection status: {}", player.getName(), connected);
    }

    /**
     * Remove a player from a room.
     * If host leaves, transfer host to another player.
     */
    public void removePlayerFromRoom(Room room, String playerId) {
        Player player = getPlayerFromRoom(room, playerId);
        room.getPlayers().remove(player);

        // Transfer host if needed
        if (player.isHost() && !room.getPlayers().isEmpty()) {
            Player newHost = room.getPlayers().get(0);
            newHost.setHost(true);
            room.setHostPlayerId(newHost.getPlayerId());
            log.info("Host transferred to {} in room {}", newHost.getName(), room.getRoomCode());
        }

        room.setLastActivityAt(Instant.now());
        log.debug("Player {} removed from room {}", player.getName(), room.getRoomCode());
    }

    /**
     * Check if all players are connected.
     * Used before starting a game to ensure everyone is ready.
     */
    public boolean areAllPlayersConnected(Room room) {
        return room.getPlayers().stream().allMatch(Player::isConnected);
    }
}