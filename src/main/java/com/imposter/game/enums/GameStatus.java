package com.imposter.game.enums;

public enum GameStatus {
    WAITING,      // Room created, players can join
    STARTING,     // Game is being initialized (brief transition state)
    PLAYING,      // Active round in progress
    ROUND_ENDED,  // Round completed, waiting for next round
    GAME_ENDED    // Game over, room can be cleaned up
}