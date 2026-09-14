package com.imposter.game.dto.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketEvent<T> {

    private EventType eventType;
    private T data;
    private String timestamp;  // Changed from Instant to String
    private String roomCode;

    public enum EventType {
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYER_RECONNECTED,
        GAME_STARTED,
        ROUND_STARTED,
        ROUND_ENDED,
        GAME_ENDED,
        ROOM_UPDATED,
        ERROR
    }

    // Factory methods
    public static <T> WebSocketEvent<T> of(EventType type, T data, String roomCode) {
        return WebSocketEvent.<T>builder()
                .eventType(type)
                .data(data)
                .roomCode(roomCode)
                .timestamp(Instant.now().toString())
                .build();
    }
}