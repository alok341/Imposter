package com.imposter.game.dto.game;

import com.imposter.game.enums.PlayerRole;
import com.imposter.game.model.CurrentRound;
import com.imposter.game.model.Room;
import com.imposter.game.model.WordSet;

/**
 * Private player assignment - only returned to the specific player.
 *
 * SECURITY: This DTO contains secret game information and must only
 * be returned from the assignment endpoint after proper validation.
 */

public record AssignmentResponse(
        PlayerRole role,
        String word,
        int roundNumber,
        String category,
        String difficulty
) {
    public static AssignmentResponse fromEntities(
            Room room,
            WordSet wordSet,
            String playerId) {

        CurrentRound round = room.getCurrentRound();
        PlayerRole role = round.isImposter(playerId)
                ? PlayerRole.IMPOSTER
                : PlayerRole.PLAYER;

        String word = role == PlayerRole.IMPOSTER
                ? wordSet.getImposterWord()
                : wordSet.getNormalWord();

        return new AssignmentResponse(
                role,
                word,
                round.getRoundNumber(),
                wordSet.getCategory(),
                wordSet.getDifficulty().name()
        );
    }
}