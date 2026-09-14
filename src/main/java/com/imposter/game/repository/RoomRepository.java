package com.imposter.game.repository;

import com.imposter.game.enums.GameStatus;
import com.imposter.game.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing temporary game rooms.
 *
 * Rooms are temporary sessions that get cleaned up after use.
 */
@Repository
public interface RoomRepository extends MongoRepository<Room, String> {

    /**
     * Find a room by its unique room code.
     * Room codes are unique among active rooms.
     */
    Optional<Room> findByRoomCode(String roomCode);

    /**
     * Check if a room code exists among active rooms.
     * Used during room code generation to ensure uniqueness.
     */
    boolean existsByRoomCode(String roomCode);

    /**
     * Find all active rooms.
     * Useful for monitoring and cleanup.
     */
    List<Room> findByStatusIn(List<GameStatus> activeStatuses);

    /**
     * Find rooms that haven't been active since a certain time.
     * Used for automatic cleanup of abandoned rooms.
     */
    List<Room> findByLastActivityAtBefore(Instant cutoffTime);

    /**
     * Delete rooms that have expired.
     * Cleanup operation for inactive rooms.
     */
    void deleteByLastActivityAtBefore(Instant cutoffTime);

    /**
     * Count active rooms.
     * For monitoring and analytics.
     */
    long countByStatusIn(List<GameStatus> activeStatuses);

    /**
     * Find room by player ID.
     * Used for reconnection scenarios.
     */
    @Query("{ 'players.playerId': ?0, 'status': { $in: ['WAITING', 'PLAYING', 'ROUND_ENDED'] } }")
    Optional<Room> findActiveRoomByPlayerId(String playerId);

    /**
     * Update room activity timestamp.
     * Keeps rooms alive when players are active.
     */
    @Query("{ '_id': ?0 }")
    void updateLastActivity(String roomId, Instant timestamp);
}