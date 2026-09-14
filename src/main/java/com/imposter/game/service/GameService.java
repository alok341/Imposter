package com.imposter.game.service;

import com.imposter.game.dto.game.AssignmentResponse;
import com.imposter.game.dto.game.GameStateResponse;
import com.imposter.game.enums.GameStatus;
import com.imposter.game.exception.*;
import com.imposter.game.model.*;
import com.imposter.game.repository.RoomRepository;
import com.imposter.game.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final WordService wordService;
    private final RandomUtil randomUtil;
    private final WebSocketNotificationService notificationService;

    /**
     * Start a new game.
     */
    @Transactional
    public GameStateResponse startGame(String roomCode, String hostPlayerId, Integer imposterCount) {
        Room room = roomService.getRoomByCode(roomCode);

        // Validate host
        roomService.validateHost(room, hostPlayerId);

        // Validate game state
        if (room.getStatus() != GameStatus.WAITING) {
            throw new InvalidGameStateException(
                    room.getStatus().name(),
                    GameStatus.WAITING.name(),
                    "start game"
            );
        }

        // Validate player count
        if (room.getPlayers().size() < InsufficientPlayersException.MIN_PLAYERS) {
            throw new InsufficientPlayersException(room.getPlayers().size());
        }

        // Validate imposter count
        int finalImposterCount = validateAndGetImposterCount(room, imposterCount);
        room.getSettings().setImposterCount(finalImposterCount);

        // Check word availability
        if (!wordService.hasAvailableWordSets()) {
            throw new NoWordSetAvailableException();
        }

        // Set game to STARTING
        room.setStatus(GameStatus.STARTING);
        roomRepository.save(room);

        // Create first round
        createNewRound(room);

        // Set game to PLAYING
        room.setStatus(GameStatus.PLAYING);
        room.setLastActivityAt(Instant.now());

        // Save room with all changes
        Room savedRoom = roomRepository.save(room);

        // Send WebSocket notification
        notificationService.notifyGameStarted(savedRoom);

        log.info("Game started in room {} with {} imposters. Round: {}",
                roomCode, finalImposterCount,
                savedRoom.getCurrentRound() != null ? savedRoom.getCurrentRound().getRoundNumber() : 0);

        return GameStateResponse.fromEntity(savedRoom);
    }

    /**
     * Create a new round with fresh word set and imposter selection.
     */
    private void createNewRound(Room room) {
        try {
            // Select word set
            WordSet wordSet = wordService.selectWordSet(room);
            log.debug("Selected word set: {}", wordSet.getId());

            // Compute the new round number BEFORE selecting imposters
            // (selectImposters needs to know the current round to record it)
            int nextRound = getNextRoundNumber(room);

            // Select imposters
            List<String> imposterIds = selectImposters(room, nextRound);
            log.debug("Selected imposters: {}", imposterIds);

            // Create current round
            CurrentRound round = CurrentRound.builder()
                    .roundNumber(nextRound)
                    .wordSetId(wordSet.getId())
                    .imposterIds(imposterIds)
                    .build();

            room.setCurrentRound(round);
            log.debug("Created round {} in room {}", round.getRoundNumber(), room.getRoomCode());

        } catch (Exception e) {
            log.error("Failed to create round for room {}", room.getRoomCode(), e);
            throw e;
        }
    }

    /**
     * Select imposters for the current round.
     *
     * Strategy:
     * 1. Prefer players who were NOT imposter in the previous round.
     * 2. Fall back to full pool if not enough eligible players (defensive).
     * 3. Randomly pick from eligible pool.
     * 4. Record the round number on each chosen player.
     *
     * Guarantee: with 4+ players and 1-3 imposters, no player is imposter two rounds in a row.
     */
    private List<String> selectImposters(Room room, int currentRound) {
        int imposterCount = room.getSettings().getImposterCount();
        int previousRound = currentRound - 1;

        log.debug("Selecting {} imposters for round {} from {} players",
                imposterCount, currentRound, room.getPlayers().size());

        // Build pool of eligible players (exclude last round's imposters)
        List<Player> eligiblePlayers = room.getPlayers().stream()
                .filter(p -> previousRound <= 0 || p.getLastImposterRound() != previousRound)
                .collect(Collectors.toList());

        // Safety: if not enough eligible players, fall back to full pool
        if (eligiblePlayers.size() < imposterCount) {
            log.warn("Not enough eligible players for round {} (have {}, need {}). Falling back to full pool.",
                    currentRound, eligiblePlayers.size(), imposterCount);
            eligiblePlayers = new ArrayList<>(room.getPlayers());
        }

        // Randomly select imposters from eligible pool
        List<Player> selectedPlayers = randomUtil.selectRandomMultiple(eligiblePlayers, imposterCount);

        // Update each chosen player's lastImposterRound and extract IDs
        List<String> imposterIds = new ArrayList<>();
        for (Player p : selectedPlayers) {
            p.setLastImposterRound(currentRound);
            imposterIds.add(p.getPlayerId());
            log.debug("Player {} ({}) is imposter for round {}",
                    p.getName(), p.getPlayerId(), currentRound);
        }

        return imposterIds;
    }

    /**
     * Get player assignment.
     */
    public AssignmentResponse getAssignment(String roomCode, String playerId) {
        Room room = roomService.getRoomByCode(roomCode);

        // Validate player belongs to room
        Player player = room.getPlayerById(playerId);
        if (player == null) {
            throw new PlayerNotFoundException(playerId, roomCode);
        }

        // Validate game has started
        if (room.getStatus() != GameStatus.PLAYING && room.getStatus() != GameStatus.ROUND_ENDED) {
            throw new InvalidGameStateException(
                    "Cannot get assignment before game starts. Current status: " + room.getStatus()
            );
        }

        // Validate current round exists
        if (room.getCurrentRound() == null) {
            log.error("Room {} has no current round despite being in {} state", roomCode, room.getStatus());
            throw new InvalidGameStateException("No active round in this game");
        }

        // Get word set
        WordSet wordSet = wordService.getWordSetById(room.getCurrentRound().getWordSetId());

        // Create assignment
        return AssignmentResponse.fromEntities(room, wordSet, playerId);
    }

    /**
     * Start next round.
     */
    @Transactional
    public GameStateResponse nextRound(String roomCode, String hostPlayerId) {
        Room room = roomService.getRoomByCode(roomCode);

        // Validate host
        roomService.validateHost(room, hostPlayerId);

        // Validate game state
        if (room.getStatus() != GameStatus.PLAYING && room.getStatus() != GameStatus.ROUND_ENDED) {
            throw new InvalidGameStateException(
                    room.getStatus().name(),
                    "PLAYING or ROUND_ENDED",
                    "start next round"
            );
        }

        // Create new round
        createNewRound(room);

        // Update game state
        room.setStatus(GameStatus.PLAYING);
        room.setLastActivityAt(Instant.now());
        Room savedRoom = roomRepository.save(room);

        // Send WebSocket notification
        notificationService.notifyRoundStarted(savedRoom);

        log.info("Started round {} in room {}",
                savedRoom.getCurrentRound().getRoundNumber(), roomCode);

        return GameStateResponse.fromEntity(savedRoom);
    }

    /**
     * End the game.
     */
    @Transactional
    public GameStateResponse endGame(String roomCode, String hostPlayerId) {
        Room room = roomService.getRoomByCode(roomCode);

        // Validate host
        roomService.validateHost(room, hostPlayerId);

        // Validate game state
        if (room.getStatus() == GameStatus.GAME_ENDED) {
            throw new InvalidGameStateException("Game has already ended");
        }

        // Update room state
        room.setStatus(GameStatus.GAME_ENDED);
        room.setCurrentRound(null);
        room.setLastActivityAt(Instant.now());
        Room savedRoom = roomRepository.save(room);

        // Send WebSocket notification
        notificationService.notifyGameEnded(savedRoom);

        log.info("Game ended in room {}", roomCode);

        return GameStateResponse.fromEntity(savedRoom);
    }

    /**
     * Validate and determine final imposter count.
     */
    private int validateAndGetImposterCount(Room room, Integer requestedCount) {
        int playerCount = room.getPlayers().size();

        if (requestedCount != null) {
            if (!isValidImposterCount(playerCount, requestedCount)) {
                throw new IllegalArgumentException(
                        String.format("Invalid imposter count %d for %d players",
                                requestedCount, playerCount)
                );
            }
            return requestedCount;
        }

        return getDefaultImposterCount(playerCount);
    }

    /**
     * Get default imposter count based on player count.
     */
    private int getDefaultImposterCount(int playerCount) {
        if (playerCount <= 7) {
            return 1;
        } else if (playerCount <= 10) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Validate if an imposter count is appropriate for player count.
     */
    private boolean isValidImposterCount(int playerCount, int imposterCount) {
        if (imposterCount < 1) {
            return false;
        }

        if (imposterCount >= playerCount / 2) {
            return false;
        }

        return imposterCount <= 3;
    }

    /**
     * Get next round number.
     */
    private int getNextRoundNumber(Room room) {
        return room.getCurrentRound() != null
                ? room.getCurrentRound().getRoundNumber() + 1
                : 1;
    }
}
