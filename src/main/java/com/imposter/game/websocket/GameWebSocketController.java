package com.imposter.game.websocket;

import com.imposter.game.dto.websocket.WebSocketEvent;
import com.imposter.game.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket controller for real-time game events.
 * Handles client subscriptions and message routing.
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle client connection to a room.
     * Client sends message to /app/rooms/{roomCode}/connect
     */
    @MessageMapping("/rooms/{roomCode}/connect")
    public void connectToRoom(
            @DestinationVariable String roomCode,
            Principal principal) {

        log.debug("Client connected to room {}: {}", roomCode,
                principal != null ? principal.getName() : "anonymous");

        // Send acknowledgment
        sendToRoom(roomCode, WebSocketEvent.of(
                WebSocketEvent.EventType.ROOM_UPDATED,
                "Client connected",
                roomCode
        ));
    }

    /**
     * Handle player ready status.
     * Client sends message to /app/rooms/{roomCode}/ready
     */
    @MessageMapping("/rooms/{roomCode}/ready")
    public void playerReady(
            @DestinationVariable String roomCode,
            @Payload ReadyPayload payload) {

        log.debug("Player {} ready in room {}", payload.playerId(), roomCode);

        // Here you would update player ready status
        // For now, just broadcast the event
        sendToRoom(roomCode, WebSocketEvent.of(
                WebSocketEvent.EventType.ROOM_UPDATED,
                "Player ready: " + payload.playerId(),
                roomCode
        ));
    }

    /**
     * Handle ping to keep connection alive.
     */
    @MessageMapping("/rooms/{roomCode}/ping")
    public void ping(@DestinationVariable String roomCode) {
        // Client is still connected, no action needed
        // Could update lastSeenAt in future
    }

    /**
     * Send message to all subscribers of a room topic.
     */
    public void sendToRoom(String roomCode, WebSocketEvent<?> event) {
        messagingTemplate.convertAndSend(
                "/topic/rooms/" + roomCode,
                event
        );
    }

    /**
     * Send message to a specific user.
     */
    public void sendToUser(String user, WebSocketEvent<?> event) {
        messagingTemplate.convertAndSendToUser(
                user,
                "/queue/private",
                event
        );
    }

    /**
     * Payload for ready status.
     */
    private record ReadyPayload(String playerId, boolean ready) {}
}