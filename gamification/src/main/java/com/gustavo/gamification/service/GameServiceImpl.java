package com.gustavo.gamification.service;

import com.gustavo.gamification.client.MultiplicationResultAttemptClientImpl;
import com.gustavo.gamification.client.dto.MultiplicationResultAttempt;
import com.gustavo.gamification.domain.Badge;
import com.gustavo.gamification.domain.BadgeCard;
import com.gustavo.gamification.domain.GameStats;
import com.gustavo.gamification.domain.ScoreCard;
import com.gustavo.gamification.repository.BadgeCardRepository;
import com.gustavo.gamification.repository.ScoreCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GameServiceImpl implements GameService {
    public static final int LUCKY_NUMBER = 42;

    private ScoreCardRepository scoreCardRepository;
    private BadgeCardRepository badgeCardRepository;
    private MultiplicationResultAttemptClientImpl attemptClient;

    public GameServiceImpl(ScoreCardRepository scoreCardRepository, BadgeCardRepository badgeCardRepository, MultiplicationResultAttemptClientImpl attemptClient) {
        this.scoreCardRepository = scoreCardRepository;
        this.badgeCardRepository = badgeCardRepository;
        this.attemptClient = attemptClient;
    }

    @Override
    public GameStats newAttemptForUser(Long userId, Long attemptId, boolean correct) {
        if (correct) {
            ScoreCard card = new ScoreCard(userId, attemptId);
            scoreCardRepository.save(card);
            log.info("User with id {} scored {} points for attempt id {}", userId, card.getScore(), attemptId);

            List<BadgeCard> badgeCards = processForBadges(userId, attemptId);
            return new GameStats(userId, card.getScore(),
                    badgeCards.stream()
                            .map(BadgeCard::getBadge)
                            .collect(Collectors.toList()));


        } else {
            return GameStats.emptyStats(userId);
        }
    }

    private List<BadgeCard> processForBadges(Long userId, Long attemptId) {
        List<BadgeCard> badgeCards = new ArrayList<>();

        int totalScore = scoreCardRepository.getTotalScoreForUser(userId);
        log.info("New score for user {} is {}", userId, totalScore);

        List<ScoreCard> scoreCardList = scoreCardRepository.findByUserIdOrderByScoreTimestampDesc(userId);
        List<BadgeCard> badgeCardList = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);

        checkAndGiveBasedOnScore(badgeCardList, Badge.BRONZE_MULTIPLICATOR, totalScore, 100, userId)
                .ifPresent(badgeCards::add);
        checkAndGiveBasedOnScore(badgeCardList, Badge.SILVER_MULTIPLICATOR, totalScore, 500, userId)
                .ifPresent(badgeCards::add);
        checkAndGiveBasedOnScore(badgeCardList, Badge.GOLD_MULTIPLICATOR, totalScore, 999, userId)
                .ifPresent(badgeCards::add);
        //implement other badges

        // First won badge
        if (scoreCardList.size() == 1 &&
                !containsBadge(badgeCardList, Badge.
                        FIRST_WON)) {
            BadgeCard firstWonBadge = giveBadgeToUser(Badge.
                    FIRST_WON, userId);
            badgeCards.add(firstWonBadge);
        }

        //lucky badge
        MultiplicationResultAttempt attempt = attemptClient.retrieveMultiplicationResultAttemptById(attemptId);
        if (!containsBadge(badgeCardList, Badge.LUCKY_NUMBER) &&
                (LUCKY_NUMBER == attempt.getMultiplicationFactorA()
                        || LUCKY_NUMBER == attempt.getMultiplicationFactorB())) {
            BadgeCard luckyBadgeCard = giveBadgeToUser(Badge.LUCKY_NUMBER, userId);
            badgeCards.add(luckyBadgeCard);
        }

        return badgeCards;
    }

    private BadgeCard giveBadgeToUser(Badge badge, Long userId) {
        BadgeCard badgeCard = new BadgeCard(userId, badge);
        badgeCardRepository.save(badgeCard);
        log.info("User with id {} won a new badge: {}", userId, badge);
        return badgeCard;

       /* log.info("User with id {} won a new badge: {}", userId, badge);
        return badgeCardRepository.save( new BadgeCard(userId, badge));*/
    }

    /**
     * Checks if the passed list of badges includes the one
     * being checked
     */
    private boolean containsBadge(List<BadgeCard> badgeCardList, Badge badge) {
        return badgeCardList.stream().anyMatch(b -> b.getBadge().equals(badge));
    }

    /**
     * Convenience method to check the current score against
     * the different thresholds to gain badges.
     * It also assigns badge to user if the conditions are met.
     */
    private Optional<BadgeCard> checkAndGiveBasedOnScore(
            List<BadgeCard> badgeCardList, Badge badge, int totalScore, int scoreThreshold, Long userId) {

        if (totalScore >= scoreThreshold && !containsBadge(badgeCardList, badge)) {
            return Optional.of(giveBadgeToUser(badge, userId));
        }

        return Optional.empty();
    }


    @Override
    public GameStats retrieveStatsForUser(Long userId) {
        int score = scoreCardRepository.getTotalScoreForUser(userId);
        List<BadgeCard> badgeCards = badgeCardRepository.findByUserIdOrderByBadgeTimestampDesc(userId);
        return new GameStats(userId, score,
                badgeCards.stream()
                        .map(BadgeCard::getBadge)
                        .collect(Collectors.toList()));
    }
}
