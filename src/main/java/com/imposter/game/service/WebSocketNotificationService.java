package com.imposter.game.service;

import com.imposter.game.dto.game.GameStateResponse;
import com.imposter.game.dto.player.PlayerResponse;
import com.imposter.game.dto.websocket.GameEventData;
import com.imposter.game.dto.websocket.PlayerEventData;
import com.imposter.game.dto.websocket.WebSocketEvent;
import com.imposter.game.model.Player;
import com.imposter.game.model.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for sending WebSocket notifications.
 * Centralizes all real-time event broadcasting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notify all players that someone joined.
     */
    public void notifyPlayerJoined(Room room, Player player) {
        PlayerResponse playerResponse = PlayerResponse.fromEntity(player);
        PlayerEventData data = PlayerEventData.from(
                playerResponse,
                room.getPlayers().size(),
                room.getSettings().getPlayerLimit()
        );

        WebSocketEvent<PlayerEventData> event = WebSocketEvent.of(
                WebSocketEvent.EventType.PLAYER_JOINED,
                data,
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
        log.debug("Player joined event sent for room {}", room.getRoomCode());
    }

    /**
     * Notify all players that someone left.
     */
    public void notifyPlayerLeft(Room room, Player player) {
        PlayerResponse playerResponse = PlayerResponse.fromEntity(player);
        PlayerEventData data = PlayerEventData.from(
                playerResponse,
                room.getPlayers().size(),
                room.getSettings().getPlayerLimit()
        );

        WebSocketEvent<PlayerEventData> event = WebSocketEvent.of(
                WebSocketEvent.EventType.PLAYER_LEFT,
                data,
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
        log.debug("Player left event sent for room {}", room.getRoomCode());
    }

    /**
     * Notify all players that game has started.
     */
    public void notifyGameStarted(Room room) {
        GameStateResponse gameState = GameStateResponse.fromEntity(room);
        GameEventData data = GameEventData.from(gameState);

        WebSocketEvent<GameEventData> event = WebSocketEvent.of(
                WebSocketEvent.EventType.GAME_STARTED,
                data,
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
        log.debug("Game started event sent for room {}", room.getRoomCode());
    }

    /**
     * Notify all players that a new round has started.
     */
    public void notifyRoundStarted(Room room) {
        GameStateResponse gameState = GameStateResponse.fromEntity(room);
        GameEventData data = GameEventData.from(gameState);

        WebSocketEvent<GameEventData> event = WebSocketEvent.of(
                WebSocketEvent.EventType.ROUND_STARTED,
                data,
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
        log.debug("Round started event sent for room {}", room.getRoomCode());
    }

    /**
     * Notify all players that game has ended.
     */
    public void notifyGameEnded(Room room) {
        GameStateResponse gameState = GameStateResponse.fromEntity(room);
        GameEventData data = GameEventData.from(gameState);

        WebSocketEvent<GameEventData> event = WebSocketEvent.of(
                WebSocketEvent.EventType.GAME_ENDED,
                data,
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
        log.debug("Game ended event sent for room {}", room.getRoomCode());
    }

    /**
     * Notify all players of general room update.
     */
    public void notifyRoomUpdated(Room room) {
        WebSocketEvent<String> event = WebSocketEvent.of(
                WebSocketEvent.EventType.ROOM_UPDATED,
                "Room updated",
                room.getRoomCode()
        );

        sendToRoom(room.getRoomCode(), event);
    }

    /**
     * Send message to a room topic.
     */
    private void sendToRoom(String roomCode, WebSocketEvent<?> event) {
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + roomCode,
                event
        );
    }
}