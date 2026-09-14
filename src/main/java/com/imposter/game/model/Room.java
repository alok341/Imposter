package com.imposter.game.model;

import com.imposter.game.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a temporary game session.
 *
 * A room is created by a host, players join, and the game proceeds
 * through multiple rounds until the host ends it or the room expires.
 */
@Document(collection = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    private String id;

    @Indexed(unique = true)
    private String roomCode;

    private String hostPlayerId;

    private GameStatus status;

    private GameSettings settings;

    private List<Player> players;

    private CurrentRound currentRound;

    private List<String> usedWordSetIds;

    private Instant createdAt;

    private Instant lastActivityAt;

    // Helper methods

    public Player getPlayerById(String playerId) {
        if (players == null) return null;
        return players.stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    public boolean isHost(String playerId) {
        return hostPlayerId != null && hostPlayerId.equals(playerId);
    }

    public int getConnectedPlayerCount() {
        if (players == null) return 0;
        return (int) players.stream()
                .filter(Player::isConnected)
                .count();
    }

    public boolean isFull() {
        if (settings == null || players == null) return false;
        return players.size() >= settings.getPlayerLimit();
    }

    public boolean canStartGame() {
        return status == GameStatus.WAITING
                && players != null
                && players.size() >= 4;
    }

    // Builder customization for initialization
    public static Room createRoom(String roomCode, String hostPlayerId, Player hostPlayer) {
        return Room.builder()
                .roomCode(roomCode)
                .hostPlayerId(hostPlayerId)
                .status(GameStatus.WAITING)
                .settings(GameSettings.defaultSettings())
                .players(new ArrayList<>(List.of(hostPlayer)))
                .usedWordSetIds(new ArrayList<>())
                .createdAt(Instant.now())
                .lastActivityAt(Instant.now())
                .build();
    }
}