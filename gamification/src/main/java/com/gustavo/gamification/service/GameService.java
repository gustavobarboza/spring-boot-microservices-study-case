package com.gustavo.gamification.service;

import com.gustavo.gamification.domain.GameStats;

/**
 * This service includes the main logic for gamifying the
 * system.
 */
public interface GameService {
    /**
     * Process a new attempt from a given user
     *
     * @param userId    the user id
     * @param attemptId the attempt id
     * @param correct   indicates if the attempt was correct
     * @return a {@link GameStats} object containing the new score
     * and game badges obtained
     */
    GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct);

    /**
     * Gets the statistics for a given user
     *
     * @param userId the user id
     * @return the total statistics for that user
     */
    GameStats retrieveStatsForUser(Long userId);
}
